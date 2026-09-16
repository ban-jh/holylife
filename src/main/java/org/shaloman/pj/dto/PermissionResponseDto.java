package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 권한 응답 DTO
 */
@Data
@Schema(description = "권한 응답 DTO")
public class PermissionResponseDto {

    @Schema(description = "권한 ID", example = "1")
    private Long permissionId;

    @Schema(description = "권한 그룹 ID", example = "1")
    private Long roleId;

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "읽기 권한", example = "true")
    private Boolean canRead;

    @Schema(description = "쓰기 권한", example = "false")
    private Boolean canWrite;

    @Schema(description = "삭제 권한", example = "false")
    private Boolean canDelete;

    @Schema(description = "생성일시", example = "2026-09-01T10:00:00")
    private LocalDateTime createdAt;
}