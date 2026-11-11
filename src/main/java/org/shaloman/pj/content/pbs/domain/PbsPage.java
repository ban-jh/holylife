package org.shaloman.pj.content.pbs.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PBS 페이지 도메인 (pbs_page 테이블 매핑).
 * Confluence 스타일 문서 페이지를 나타낸다.
 */
@Data
public class PbsPage {

    /** 페이지 ID (PK, auto-increment) */
    private Long pageId;

    /** 폴더 ID (FK) */
    private Long folderId;

    /** 사용자 ID (작성자, FK) */
    private Long userId;

    /** 제목 (기본값: '새 페이지') */
    private String title;

    /** 본문 내용 (longtext) */
    private String content;

    /** 상태 (DRAFT / PUBLISHED, 기본값: DRAFT) */
    private String status;

    /** 구분 (OT / NT, testament) */
    private String testament;

    /** 성경 책 코드 */
    private String bookCode;

    /** 시작 장 */
    private Integer chapterFrom;

    /** 시작 절 */
    private Integer verseFrom;

    /** 끝 장 */
    private Integer chapterTo;

    /** 끝 절 */
    private Integer verseTo;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 배포일시 */
    private LocalDateTime publishedAt;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    // ── 연관 데이터 ──

    /** 첨부 참조 목록 (URL / IMAGE / DOCUMENT) */
    private List<PbsReference> references;
}
