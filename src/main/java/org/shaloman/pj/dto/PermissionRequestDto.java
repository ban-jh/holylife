package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 권한 등록/수정 요청 DTO
 */
@Data
@Schema(description = "권한 요청 DTO")
public class PermissionRequestDto {

    @Schema(description = "권한 그룹 ID (FK)", example = "1", required = true)
    @NotNull(message = "권한 그룹 ID는 필수입니다")
    private Long roleId;

    @Schema(description = "메뉴 ID (FK)", example = "1", required = true)
    @NotNull(message = "메뉴 ID는 필수입니다")
    private Long menuId;

    @Schema(description = "읽기 권한", example = "true")
    private Boolean canRead;

    @Schema(description = "쓰기 권한", example = "false")
    private Boolean canWrite;

    @Schema(description = "삭제 권한", example = "false")
    private Boolean canDelete;
}