package org.shaloman.pj.system.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.shaloman.pj.config.JwtUtil;
import org.shaloman.pj.system.user.domain.User;
import org.shaloman.pj.system.user.dto.LoginRequestDto;
import org.shaloman.pj.system.user.dto.LoginResponseDto;
import org.shaloman.pj.system.user.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인증 RESTful API 컨트롤러.
 * 로그인 및 현재 사용자 정보 조회를 담당한다.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 관리", description = "로그인 및 사용자 인증 API")
public class AuthController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 취약점 #9: 로그인 시도 제한 (in-memory rate limiter)
    // IP별 실패 시간 기록: 1분에 5회 이상 실패 시 60초간 차단
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 60_000L; // 1분
    private static final long ATTEMPT_WINDOW_MS = 60_000L;   // 1분
    private final ConcurrentHashMap<String, List<Long>> failedAttempts = new ConcurrentHashMap<>();

    public AuthController(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받는다.")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request,
                                                   HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);

        // 취약점 #9: 로그인 시도 제한 확인
        if (isLockedOut(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            recordFailedAttempt(clientIp);
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 계정 상태 확인
        if (!"A".equals(user.getAccountStatus())) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            recordFailedAttempt(clientIp);
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 실패 기록 초기화
        failedAttempts.remove(clientIp);

        // 로그인 시간 업데이트
        user.setLastLogin(LocalDateTime.now());
        userMapper.update(user);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRoleGroup());

        // 응답 DTO 구성
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);

        LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setEmail(user.getEmail());
        userInfo.setName(user.getName());
        userInfo.setNickname(user.getNickname());
        userInfo.setRoleGroup(user.getRoleGroup());
        response.setUser(userInfo);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보", description = "JWT 토큰에서 추출한 현재 로그인 사용자 정보를 반환한다.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<LoginResponseDto.UserInfo> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = (String) auth.getPrincipal();
        User user = userMapper.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setEmail(user.getEmail());
        userInfo.setName(user.getName());
        userInfo.setNickname(user.getNickname());
        userInfo.setRoleGroup(user.getRoleGroup());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * 취약점 #9: IP가 현재 차단 상태인지 확인.
     */
    private boolean isLockedOut(String ip) {
        List<Long> attempts = failedAttempts.get(ip);
        if (attempts == null || attempts.isEmpty()) {
            return false;
        }
        long now = System.currentTimeMillis();
        // 최근 ATTEMPT_WINDOW_MS 이내의 실패만 카운트
        List<Long> recent = new ArrayList<>();
        for (Long t : attempts) {
            if (now - t < ATTEMPT_WINDOW_MS) {
                recent.add(t);
            }
        }
        if (recent.size() >= MAX_FAILED_ATTEMPTS) {
            // 마지막 실패 후 LOCKOUT_DURATION_MS가 지났으면 차단 해제
            long lastAttempt = recent.get(recent.size() - 1);
            if (now - lastAttempt >= LOCKOUT_DURATION_MS) {
                failedAttempts.remove(ip);
                return false;
            }
            return true;
        }
        // 만료된 기록 정리
        if (recent.size() != attempts.size()) {
            failedAttempts.put(ip, recent);
        }
        return false;
    }

    /**
     * 취약점 #9: 실패 시간 기록.
     */
    private void recordFailedAttempt(String ip) {
        long now = System.currentTimeMillis();
        failedAttempts.compute(ip, (key, attempts) -> {
            List<Long> list = attempts != null ? new ArrayList<>(attempts) : new ArrayList<>();
            list.add(now);
            // 최근 ATTEMPT_WINDOW_MS 이내만 유지
            list.removeIf(t -> now - t >= ATTEMPT_WINDOW_MS);
            return list;
        });
    }

    /**
     * 클라이언트 IP 추출 (X-Forwarded-For 헤더 고려).
     */
    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}