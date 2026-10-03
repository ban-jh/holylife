package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.shaloman.pj.dto.UserRequestDto;
import org.shaloman.pj.dto.UserResponseDto;
import org.shaloman.pj.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 사용자 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자 관리", description = "사용자 CRUD API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "전체 사용자 조회", description = "등록된 모든 사용자 목록을 조회한다.")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 상세 조회", description = "사용자 ID로 단일 사용자 정보를 조회한다.")
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록한다.")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "사용자 수정", description = "기존 사용자 정보를 수정한다.")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @GetMapping("/{userId}/settings")
    @Operation(summary = "사용자 설정 조회", description = "사용자의 성경 버전 및 QT 출처 설정을 조회한다.")
    public ResponseEntity<UserResponseDto> getUserSettings(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/{userId}/settings")
    @Operation(summary = "사용자 설정 변경", description = "사용자의 성경 버전 및 QT 출처 설정을 변경한다.")
    public ResponseEntity<UserResponseDto> updateSettings(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        UserRequestDto request = new UserRequestDto();
        request.setBibleVersion(body.get("bibleVersion"));
        request.setQtSource(body.get("qtSource"));
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경한다.")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        userService.changePassword(userId, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "사용자 삭제", description = "사용자 ID로 사용자를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}