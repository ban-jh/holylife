package org.shaloman.pj.service;

import org.shaloman.pj.domain.User;
import org.shaloman.pj.dto.UserRequestDto;
import org.shaloman.pj.dto.UserResponseDto;
import org.shaloman.pj.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 전체 사용자 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userMapper.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID로 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }
        return toResponseDto(user);
    }

    /**
     * 사용자 등록
     */
    public UserResponseDto createUser(UserRequestDto request) {
        // 이메일 중복 체크
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRoleGroup(request.getRoleGroup() != null ? request.getRoleGroup() : "USER");
        user.setAccountStatus("ACTIVE");
        user.setMemo(request.getMemo());
        user.setEmailNotification(false);
        user.setTwoFactorAuth(false);
        user.setSessionAutoExpiry(false);
        user.setActivityLog(false);

        userMapper.insert(user);

        return toResponseDto(userMapper.findById(user.getUserId()));
    }

    /**
     * 사용자 수정
     */
    public UserResponseDto updateUser(Long userId, UserRequestDto request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        if (request.getName() != null) user.setName(request.getName());
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRoleGroup() != null) user.setRoleGroup(request.getRoleGroup());
        if (request.getMemo() != null) user.setMemo(request.getMemo());

        userMapper.update(user);

        return toResponseDto(userMapper.findById(userId));
    }

    /**
     * 사용자 삭제
     */
    public void deleteUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }
        userMapper.deleteById(userId);
    }

    // ── 변환 메서드 ──

    private UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setRoleGroup(user.getRoleGroup());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}