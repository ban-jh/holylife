package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 공통 코드 도메인 (common_codes 테이블 매핑)
 * 그룹 코드 하위의 상세 코드를 관리한다 (예: ACTIVE, DORMANT 등).
 */
@Data
public class CommonCode {

    /** 코드 ID (PK) */
    private Long codeId;

    /** 그룹 코드 (FK → group_codes.group_code) */
    private String groupCode;

    /** 코드 (예: ACTIVE) */
    private String code;

    /** 코드명 (예: 정상) */
    private String codeName;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 사용 여부 */
    private Boolean useYn;

    /** 최종 수정 일시 */
    private LocalDateTime modifiedDate;
}