package org.shaloman.pj.content.pbs.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * PBS 참조(첨부) 도메인 (pbs_reference 테이블 매핑).
 * 페이지에 첨부된 URL / IMAGE / DOCUMENT 참조를 나타낸다.
 */
@Data
public class PbsReference {

    /** 참조 ID (PK, auto-increment) */
    private Long refId;

    /** 페이지 ID (FK) */
    private Long pageId;

    /** 참조 타입 (URL / IMAGE / DOCUMENT) */
    private String refType;

    /** 참조명 */
    private String refName;

    /** 참조 URL */
    private String refUrl;

    /** 파일 경로 */
    private String filePath;

    /** 파일 크기 (bytes) */
    private Long fileSize;

    /** 생성일시 */
    private LocalDateTime createdAt;
}
