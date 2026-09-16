package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메뉴 도메인 (menus 테이블 매핑)
 * self-referencing hierarchy (parent_id → menus.menu_id)
 */
@Data
public class Menu {

    /** 메뉴 ID (PK) */
    private Long menuId;

    /** 상위 메뉴 ID (self-reference, NULL이면 1Depth 루트) */
    private Long parentId;

    /** 상위 메뉴명 (표시용) */
    private String parentName;

    /** 메뉴명 */
    private String menuName;

    /** 메뉴 코드 (예: CONTENT) */
    private String menuCode;

    /** 메뉴 URL */
    private String menuUrl;

    /** 깊이 (1: 1Depth, 2: 2Depth) */
    private Integer depth;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 아이콘 */
    private String icon;

    /** 사용 여부 */
    private Boolean useYn;

    /** 노출 여부 */
    private Boolean displayYn;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    // ── 트리 구조용 (DB 컬럼 아님) ──

    /** 하위 메뉴 목록 */
    private List<Menu> children;
}