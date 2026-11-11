package org.shaloman.pj.content.pbs.service;

import org.shaloman.pj.content.pbs.domain.PbsPage;
import org.shaloman.pj.content.pbs.mapper.PbsPageMapper;
import org.shaloman.pj.content.pbs.mapper.PbsReferenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * PBS 페이지 서비스.
 * Confluence 스타일 페이지 CRUD, 자동저장, 배포 기능을 제공한다.
 */
@Service
@Transactional
public class PbsPageService {

    private final PbsPageMapper pageMapper;
    private final PbsReferenceMapper referenceMapper;

    public PbsPageService(PbsPageMapper pageMapper, PbsReferenceMapper referenceMapper) {
        this.pageMapper = pageMapper;
        this.referenceMapper = referenceMapper;
    }

    /**
     * 페이지 ID로 상세 조회 (참조 포함, 읽기 전용)
     */
    @Transactional(readOnly = true)
    public PbsPage getPageById(Long pageId) {
        PbsPage page = pageMapper.findById(pageId);
        if (page == null) {
            throw new IllegalArgumentException("페이지를 찾을 수 없습니다: " + pageId);
        }
        return page;
    }

    /**
     * 폴더 ID로 페이지 목록 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<PbsPage> getPagesByFolder(Long folderId) {
        return pageMapper.findByFolderId(folderId);
    }

    /**
     * 임시 페이지 생성 (folder_id로 새 페이지, 기본 제목 '새 페이지', status DRAFT)
     * request 맵: folderId(필수), title(optional), userId(optional, 기본값 1)
     */
    public PbsPage createPage(Map<String, Object> request) {
        Object folderId = request.get("folderId");
        if (folderId == null) {
            throw new IllegalArgumentException("folderId는 필수입니다.");
        }
        PbsPage page = new PbsPage();
        page.setFolderId(Long.valueOf(folderId.toString()));
        // userId 기본값 1 (관리자)
        Object userId = request.get("userId");
        page.setUserId(userId != null ? Long.valueOf(userId.toString()) : 1L);
        Object title = request.get("title");
        if (title != null) page.setTitle(title.toString());
        page.setStatus("DRAFT");
        pageMapper.insert(page);
        return pageMapper.findById(page.getPageId());
    }

    /**
     * 본문 내용 수정 (자동저장용 — content만 업데이트)
     */
    public PbsPage updateContent(Long pageId, Map<String, Object> request) {
        PbsPage existing = pageMapper.findById(pageId);
        if (existing == null) {
            throw new IllegalArgumentException("페이지를 찾을 수 없습니다: " + pageId);
        }
        Object content = request.get("content");
        if (content == null) {
            throw new IllegalArgumentException("content는 필수입니다.");
        }
        pageMapper.updateContent(pageId, content.toString());
        return pageMapper.findById(pageId);
    }

    /**
     * 페이지 배포 (DRAFT → PUBLISHED, published_at 자동 설정)
     */
    public PbsPage publishPage(Long pageId) {
        PbsPage existing = pageMapper.findById(pageId);
        if (existing == null) {
            throw new IllegalArgumentException("페이지를 찾을 수 없습니다: " + pageId);
        }
        pageMapper.updateStatus(pageId, "PUBLISHED");
        return pageMapper.findById(pageId);
    }

    /**
     * 페이지 전체 수정 (제목, 본문, 성경 범위 등)
     * request 맵의 필드를 도메인에 적용하여 update.
     */
    public PbsPage updatePage(Long pageId, Map<String, Object> request) {
        PbsPage existing = pageMapper.findById(pageId);
        if (existing == null) {
            throw new IllegalArgumentException("페이지를 찾을 수 없습니다: " + pageId);
        }
        applyRequestFields(existing, request);
        pageMapper.update(existing);
        return pageMapper.findById(pageId);
    }

    /**
     * 페이지 삭제 (참조도 함께 삭제)
     */
    public void deletePage(Long pageId) {
        PbsPage existing = pageMapper.findById(pageId);
        if (existing == null) {
            throw new IllegalArgumentException("페이지를 찾을 수 없습니다: " + pageId);
        }
        referenceMapper.deleteByPageId(pageId);
        pageMapper.deleteById(pageId);
    }

    // ── 변환 헬퍼 ──

    private void applyRequestFields(PbsPage page, Map<String, Object> request) {
        if (request == null) return;
        Object title = request.get("title");
        if (title != null) page.setTitle(title.toString());
        Object content = request.get("content");
        if (content != null) page.setContent(content.toString());
        Object status = request.get("status");
        if (status != null) page.setStatus(status.toString());
        Object testament = request.get("testament");
        if (testament != null) page.setTestament(testament.toString());
        Object bookCode = request.get("bookCode");
        if (bookCode != null) page.setBookCode(bookCode.toString());
        Object chapterFrom = request.get("chapterFrom");
        if (chapterFrom != null) page.setChapterFrom(Integer.valueOf(chapterFrom.toString()));
        Object verseFrom = request.get("verseFrom");
        if (verseFrom != null) page.setVerseFrom(Integer.valueOf(verseFrom.toString()));
        Object chapterTo = request.get("chapterTo");
        if (chapterTo != null) page.setChapterTo(Integer.valueOf(chapterTo.toString()));
        Object verseTo = request.get("verseTo");
        if (verseTo != null) page.setVerseTo(Integer.valueOf(verseTo.toString()));
        Object sortOrder = request.get("sortOrder");
        if (sortOrder != null) page.setSortOrder(Integer.valueOf(sortOrder.toString()));
        Object folderId = request.get("folderId");
        if (folderId != null) page.setFolderId(Long.valueOf(folderId.toString()));
    }
}
