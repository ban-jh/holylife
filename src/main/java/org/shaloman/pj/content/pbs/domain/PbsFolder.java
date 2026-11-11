package org.shaloman.pj.content.pbs.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PBS 폴더 도메인 (pbs_folder 테이블 매핑).
 * Confluence 스타일 페이지 트리 구조의 폴더를 나타낸다.
 */
@Data
public class PbsFolder {

    /** 폴더 ID (PK, auto-increment) */
    private Long folderId;

    /** 부모 폴더 ID (self-FK, nullable — 최상위 폴더는 null) */
    private Long parentId;

    /** 폴더명 */
    private String folderName;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    // ── 트리 응답용 하위 데이터 ──

    /** 하위 폴더 목록 (트리 구조) */
    private List<PbsFolder> children;

    /** 해당 폴더의 페이지 목록 */
    private List<PbsPage> pages;
}
