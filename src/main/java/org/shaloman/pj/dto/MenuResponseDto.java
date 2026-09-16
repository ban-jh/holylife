package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메뉴 응답 DTO
 */
@Data
@Schema(description = "메뉴 응답 DTO")
public class MenuResponseDto {

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "상위 메뉴 ID", example = "1")
    private Long parentId;

    @Schema(description = "상위 메뉴명", example = "콘텐츠 관리")
    private String parentName;

    @Schema(description = "메뉴명", example = "콘텐츠 관리")
    private String menuName;

    @Schema(description = "메뉴 코드", example = "CONTENT")
    private String menuCode;

    @Schema(description = "메뉴 URL", example = "/content")
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

    @Schema(description = "생성일시", example = "2026-09-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2026-09-01T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "하위 메뉴 목록 (트리 구조)")
    private List<MenuResponseDto> children;
}