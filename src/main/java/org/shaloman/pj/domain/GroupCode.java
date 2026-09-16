package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 그룹 코드 도메인 (group_codes 테이블 매핑)
 * 공통코드의 상위 그룹을 관리한다 (예: USER_STATUS, USER_ROLE 등).
 */
@Data
public class GroupCode {

    /** 그룹 코드 (PK, 예: USER_STATUS) */
    private String groupCode;

    /** 그룹 코드명 (예: 사용자 상태) */
    private String groupName;

    /** 최종 수정 일시 */
    private LocalDateTime modifiedDate;
}