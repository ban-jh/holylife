package org.shaloman.pj.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 로그인 응답 DTO
 */
@Data
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(description = "JWT 토큰")
    private String token;

    @Schema(description = "사용자 정보")
    private UserInfo user;

    @Data
    @Schema(description = "사용자 정보")
    public static class UserInfo {
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;

        @Schema(description = "이메일", example = "admin@holylife.local")
        private String email;

        @Schema(description = "이름", example = "관리자")
        private String name;

        @Schema(description = "닉네임", example = "관리자")
        private String nickname;

        @Schema(description = "권한 그룹", example = "SYS_ADMIN")
        private String roleGroup;
    }
}