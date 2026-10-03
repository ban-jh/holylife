package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 큐티(QT) 도메인 (qts 테이블 매핑).
 */
@Data
public class Qt {

    /** 큐티 ID (PK, auto-increment) */
    private Long qtId;

    /** 사용자 ID (작성자) */
    private Long userId;

    /** 큐티 제목 */
    private String title;

    /** 큐티 날짜 */
    private LocalDate qtDate;

    /** 성경 본문 참조 (예: "창세기 1:1~3") */
    private String bibleRef;

    /** 성경 본문 텍스트 */
    private String bibleText;

    /** 큐티 내용 */
    private String content;

    /** 공개 범위 (기본값: PUBLIC) */
    private String visibility;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}