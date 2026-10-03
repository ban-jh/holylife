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

    @Schema(description = "비밀번호 (등록 시 필수, 수정 시 생략 가능)", example = "password123")
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

    @Schema(description = "권한 그룹 (공통코드 user_role)", example = "SYS_ADMIN")
    private String roleGroup;

    @Schema(description = "계정 상태 (공통코드 user_status)", example = "A")
    private String accountStatus;

    @Schema(description = "메모", example = "관리자 계정")
    private String memo;

    @Schema(description = "성경 버전 (gae, sae, niv)", example = "gae")
    private String bibleVersion;

    @Schema(description = "QT 출처 (duranno, sum, manual)", example = "duranno")
    private String qtSource;
}