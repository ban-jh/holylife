package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 메뉴 권한 트리 응답 DTO
 * 특정 role_id에 대해 전체 메뉴 트리 + 체크 여부를 반환한다.
 */
@Data
@Schema(description = "메뉴 권한 트리 응답 DTO")
public class MenuPermissionResponseDto {

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "상위 메뉴 ID", example = "1")
    private Long parentId;

    @Schema(description = "메뉴명", example = "콘텐츠 관리")
    private String menuName;

    @Schema(description = "메뉴 코드", example = "CONTENT")
    private String menuCode;

    @Schema(description = "메뉴 URL", example = "/content")
    private String menuUrl;

    @Schema(description = "깊이 (1: 1Depth, 2: 2Depth)", example = "1")
    private Integer depth;

    @Schema(description = "읽기 권한 여부 (체크 상태)", example = "true")
    private Boolean canRead;

    @Schema(description = "쓰기 권한 여부 (체크 상태)", example = "false")
    private Boolean canWrite;

    @Schema(description = "삭제 권한 여부 (체크 상태)", example = "false")
    private Boolean canDelete;

    @Schema(description = "체크 여부 (UI 체크박스 상태 - can_read 기준)", example = "true")
    private Boolean checked;

    @Schema(description = "하위 메뉴 권한 목록 (트리 구조)")
    private List<MenuPermissionResponseDto> children;
}