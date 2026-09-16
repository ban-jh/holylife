package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 그룹 코드 등록/수정 요청 DTO
 */
@Data
@Schema(description = "그룹 코드 요청 DTO")
public class GroupCodeRequestDto {

    @Schema(description = "그룹 코드명 (예: 사용자 상태)", example = "사용자 상태", required = true)
    @NotBlank(message = "그룹코드명은 필수입니다")
    @Size(max = 100, message = "그룹코드명은 100자 이하여야 합니다")
    private String groupName;

    @Schema(description = "그룹 코드 (예: USER_STATUS)", example = "USER_STATUS", required = true)
    @NotBlank(message = "그룹코드는 필수입니다")
    @Size(max = 50, message = "그룹코드는 50자 이하여야 합니다")
    private String groupCode;
}