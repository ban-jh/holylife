package org.shaloman.pj.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 로그인 요청 DTO
 */
@Data
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @Schema(description = "이메일", example = "admin@holylife.local", required = true)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Schema(description = "비밀번호", example = "admin123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}