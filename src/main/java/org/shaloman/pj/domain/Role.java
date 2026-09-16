package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 권한 그룹 도메인 (roles 테이블 매핑)
 * 권한 그룹(ADMIN, MANAGER, EDITOR, USER, GUEST 등)을 관리한다.
 */
@Data
public class Role {

    /** 권한 그룹 ID (PK) */
    private Long roleId;

    /** 그룹 코드 (예: ADMIN) */
    private String groupCode;

    /** 그룹명 (예: 관리자) */
    private String groupName;

    /** 그룹 설명 */
    private String groupDesc;

    /** 사용 여부 */
    private Boolean useYn;

    /** 생성일시 */
    private LocalDateTime createdAt;
}