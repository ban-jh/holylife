package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 메뉴 등록/수정 요청 DTO
 */
@Data
@Schema(description = "메뉴 요청 DTO")
public class MenuRequestDto {

    @Schema(description = "상위 메뉴 ID (NULL이면 1Depth 루트 메뉴)", example = "1")
    private Long parentId;

    @Schema(description = "메뉴명", example = "콘텐츠 관리", required = true)
    @NotBlank(message = "메뉴명은 필수입니다")
    @Size(max = 100, message = "메뉴명은 100자 이하여야 합니다")
    private String menuName;

    @Schema(description = "메뉴 코드", example = "CONTENT", required = true)
    @NotBlank(message = "메뉴 코드는 필수입니다")
    @Size(max = 50, message = "메뉴 코드는 50자 이하여야 합니다")
    private String menuCode;

    @Schema(description = "메뉴 URL", example = "/content", required = true)
    @NotBlank(message = "메뉴 URL은 필수입니다")
    @Size(max = 255, message = "메뉴 URL은 255자 이하여야 합니다")
    private String menuUrl;

    @Schema(description = "깊이 (1: 1Depth, 2: 2Depth)", example = "1")
    private Integer depth;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "아이콘", example = "content-icon")
    private String icon;

    @Schema(description = "사용 여부", example = "true")
    private Boolean useYn;

    @Schema(description = "노출 여부", example = "true")
    private Boolean displayYn;
}