package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 */
@Data
@Schema(description = "사용자 응답 DTO")
public class UserResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "user@church.com")
    private String email;

    @Schema(description = "이름", example = "박성민")
    private String name;

    @Schema(description = "닉네임", example = "성민")
    private String nickname;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "권한 그룹", example = "USER")
    private String roleGroup;

    @Schema(description = "계정 상태", example = "ACTIVE")
    private String accountStatus;

    @Schema(description = "최근 접속 일시", example = "2026-09-15T13:00:00")
    private LocalDateTime lastLogin;

    @Schema(description = "생성일시", example = "2026-09-01T10:00:00")
    private LocalDateTime createdAt;
}