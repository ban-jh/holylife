package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 사용자 등록/수정 요청 DTO
 */
@Data
@Schema(description = "사용자 요청 DTO")
public class UserRequestDto {

    @Schema(description = "이메일 (로그인 ID)", example = "user@church.com", required = true)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Schema(description = "비밀번호 (최소 8자)", example = "password123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @Schema(description = "이름", example = "박성민", required = true)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Schema(description = "닉네임", example = "성민", required = true)
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "권한 그룹", example = "USER", allowableValues = {"SUPER_ADMIN", "ADMIN", "EDITOR", "VIEWER", "USER"})
    private String roleGroup;

    @Schema(description = "메모", example = "관리자 계정")
    private String memo;
}