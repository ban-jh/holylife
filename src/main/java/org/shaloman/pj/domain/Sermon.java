package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 설교 도메인 (sermons 테이블 매핑).
 */
@Data
public class Sermon {

    /** 설교 ID (PK, auto-increment) */
    private Long sermonId;

    /** 사용자 ID (작성자) */
    private Long userId;

    /** 설교 제목 */
    private String title;

    /** 설교 날짜 */
    private LocalDate sermonDate;

    /** 설교자 */
    private String preacher;

    /** 성경 본문 참조 (예: "요한복음 3:16") */
    private String bibleRef;

    /** 성경 본문 텍스트 */
    private String bibleText;

    /** 설교 내용 */
    private String content;

    /** 상태 (기본값: DRAFT) */
    private String status;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}