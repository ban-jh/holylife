package org.shaloman.pj.content.pbs.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * PBS 페이지 섹션 도메인 (pbs_page_section 테이블 매핑).
 * 페이지 내 영역별 페이징(관찰/해석/적용 등)을 나타낸다.
 */
@Data
public class PbsPageSection {

    /** 섹션 ID (PK, auto-increment) */
    private Long sectionId;

    /** 페이지 ID (FK) */
    private Long pageId;

    /** 섹션 제목 */
    private String sectionTitle;

    /** 본문 내용 (longtext) */
    private String content;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}
