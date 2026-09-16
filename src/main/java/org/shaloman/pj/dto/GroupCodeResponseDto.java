package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 그룹 코드 응답 DTO
 */
@Data
@Schema(description = "그룹 코드 응답 DTO")
public class GroupCodeResponseDto {

    @Schema(description = "그룹 코드", example = "USER_STATUS")
    private String groupCode;

    @Schema(description = "그룹 코드명", example = "사용자 상태")
    private String groupName;

    @Schema(description = "최종 수정 일시", example = "2026-08-24T09:20:00")
    private LocalDateTime modifiedDate;
}