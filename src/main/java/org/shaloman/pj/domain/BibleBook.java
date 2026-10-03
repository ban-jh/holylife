package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 성경 도메인 (bible_books 테이블 매핑)
 */
@Data
public class BibleBook {

    /** 성경 구절 ID (PK) */
    private Long bookId;

    /** 번역본 ID (예: 2 = 개역개정) */
    private Long versionId;

    /** 책 코드 (예: Gen, Exo) */
    private String bookCode;

    /** 책명 (한글) */
    private String bookNameKr;

    /** 책명 (영문) */
    private String bookNameEn;

    /** 구분 (OT/NT) */
    private String testament;

    /** 장 */
    private Integer chapter;

    /** 절 */
    private Integer verse;

    /** 본문 내용 */
    private String content;

    /** 생성일시 */
    private LocalDateTime createdAt;
}