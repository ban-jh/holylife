package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 큐티(QT) 일일 도메인 (qt_daily 테이블 매핑)
 * duranno.com 크롤링 결과를 저장한다.
 */
@Data
public class QtDaily {

    /** 큐티 ID (PK, auto-increment) */
    private Long qtId;

    /** 큐티 날짜 (UNIQUE) */
    private LocalDate qtDate;

    /** 성경 본문 참조 (예: "창세기 1:1~3") */
    private String bibleRef;

    /** 큐티 제목 */
    private String title;

    /** 찬송 정보 (예: "찬송가 123장") */
    private String hymnInfo;

    /** 찬송 가사 */
    private String hymnText;

    /** 성경 구절 섹션 제목 */
    private String bibleSectionTitle;

    /** 성경 본문 텍스트 */
    private String bibleText;

    /** 오늘의 기도 */
    private String prayer;

    /** 출처 (기본값: duranno) */
    private String source;

    /** 생성일시 */
    private LocalDateTime createdAt;
}