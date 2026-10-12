package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 독서 노트 도메인 (reading_notes 테이블 매핑).
 */
@Data
public class ReadingNote {

    /** 독서 노트 ID (PK, auto-increment) */
    private Long readingId;

    /** 사용자 ID (작성자) */
    private Long userId;

    /** 책 제목 */
    private String bookTitle;

    /** 독서 날짜 */
    private LocalDate readDate;

    /** 저자 */
    private String author;

    /** 번역자 */
    private String translator;

    /** 목차 */
    private String toc;

    /** 표지 이미지 URL */
    private String coverImage;

    /** 요약 */
    private String summary;

    /** 감상 */
    private String impression;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}