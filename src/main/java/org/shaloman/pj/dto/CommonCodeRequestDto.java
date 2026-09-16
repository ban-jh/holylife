package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 공통 코드 등록/수정 요청 DTO
 */
@Data
@Schema(description = "공통 코드 요청 DTO")
public class CommonCodeRequestDto {

    @Schema(description = "그룹 코드 (FK)", example = "USER_STATUS", required = true)
    @NotBlank(message = "그룹코드는 필수입니다")
    private String groupCode;

    @Schema(description = "코드 (예: ACTIVE)", example = "ACTIVE", required = true)
    @NotBlank(message = "코드는 필수입니다")
    @Size(max = 50, message = "코드는 50자 이하여야 합니다")
    private String code;

    @Schema(description = "코드명 (예: 정상)", example = "정상", required = true)
    @NotBlank(message = "코드명은 필수입니다")
    @Size(max = 100, message = "코드명은 100자 이하여야 합니다")
    private String codeName;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "사용 여부", example = "true")
    private Boolean useYn;
}