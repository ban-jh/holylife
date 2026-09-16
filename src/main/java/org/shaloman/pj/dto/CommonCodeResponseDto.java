package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 공통 코드 응답 DTO
 */
@Data
@Schema(description = "공통 코드 응답 DTO")
public class CommonCodeResponseDto {

    @Schema(description = "코드 ID", example = "101")
    private Long codeId;

    @Schema(description = "그룹 코드", example = "USER_STATUS")
    private String groupCode;

    @Schema(description = "코드", example = "ACTIVE")
    private String code;

    @Schema(description = "코드명", example = "정상")
    private String codeName;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "사용 여부", example = "true")
    private Boolean useYn;

    @Schema(description = "최종 수정 일시", example = "2026-08-24T09:20:00")
    private LocalDateTime modifiedDate;
}