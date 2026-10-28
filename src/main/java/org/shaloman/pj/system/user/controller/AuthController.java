package org.shaloman.pj.system.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Map;

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

    public AuthController(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받는다.")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 계정 상태 확인
        if (!"A".equals(user.getAccountStatus())) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

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
}