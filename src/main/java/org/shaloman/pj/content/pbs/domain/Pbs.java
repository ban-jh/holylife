package org.shaloman.pj.content.pbs.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PBS (P.B.S Bible Study) 도메인 (pbs 테이블 매핑).
 */
@Data
public class Pbs {

    /** PBS ID (PK, auto-increment) */
    private Long pbsId;

    /** 사용자 ID (작성자) */
    private Long userId;

    /** 제목 */
    private String title;

    /** PBS 날짜 */
    private LocalDate pbsDate;

    /** 성경 책 (시작) */
    private String bookFrom;

    /** 성경 책 (끝) */
    private String bookTo;

    /** 성경 본문 텍스트 */
    private String bibleText;

    /** 내용 */
    private String content;

    /** 상태 (기본값: DRAFT) */
    private String status;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}