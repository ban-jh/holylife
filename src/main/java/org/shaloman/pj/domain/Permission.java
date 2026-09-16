package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 권한 도메인 (permissions 테이블 매핑)
 * role-menu 매핑으로 읽기/쓰기/삭제 권한을 관리한다.
 */
@Data
public class Permission {

    /** 권한 ID (PK) */
    private Long permissionId;

    /** 권한 그룹 ID (FK → roles.role_id) */
    private Long roleId;

    /** 메뉴 ID (FK → menus.menu_id) */
    private Long menuId;

    /** 읽기 권한 */
    private Boolean canRead;

    /** 쓰기 권한 */
    private Boolean canWrite;

    /** 삭제 권한 */
    private Boolean canDelete;

    /** 생성일시 */
    private LocalDateTime createdAt;
}