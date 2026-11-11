package org.shaloman.pj.content.pbs.service;

import org.shaloman.pj.content.pbs.domain.PbsPageSection;
import org.shaloman.pj.content.pbs.mapper.PbsPageSectionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * PBS 페이지 섹션 서비스.
 * 영역별 페이징(관찰/해석/적용 등) 섹션 CRUD, 자동저장, 재정렬 기능을 제공한다.
 */
@Service
@Transactional
public class PbsPageSectionService {

    private final PbsPageSectionMapper sectionMapper;

    public PbsPageSectionService(PbsPageSectionMapper sectionMapper) {
        this.sectionMapper = sectionMapper;
    }

    /**
     * 페이지 ID로 섹션 목록 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<PbsPageSection> getSectionsByPageId(Long pageId) {
        return sectionMapper.findByPageId(pageId);
    }

    /**
     * 섹션 생성
     * request 맵: sectionTitle(필수), content(optional), sortOrder(optional, 기본값 0)
     */
    public PbsPageSection createSection(Long pageId, Map<String, Object> request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 본문은 필수입니다.");
        }
        Object sectionTitle = request.get("sectionTitle");
        if (sectionTitle == null) {
            throw new IllegalArgumentException("sectionTitle은 필수입니다.");
        }
        PbsPageSection section = new PbsPageSection();
        section.setPageId(pageId);
        section.setSectionTitle(sectionTitle.toString());
        Object content = request.get("content");
        if (content != null) section.setContent(content.toString());
        Object sortOrder = request.get("sortOrder");
        if (sortOrder != null) {
            section.setSortOrder(Integer.valueOf(sortOrder.toString()));
        } else {
            section.setSortOrder(0);
        }
        sectionMapper.insert(section);
        return sectionMapper.findById(section.getSectionId());
    }

    /**
     * 섹션 전체 수정 (제목, 내용, 정렬순서)
     * request 맵: sectionTitle, content, sortOrder (각각 optional)
     */
    public PbsPageSection updateSection(Long sectionId, Map<String, Object> request) {
        PbsPageSection existing = sectionMapper.findById(sectionId);
        if (existing == null) {
            throw new IllegalArgumentException("섹션을 찾을 수 없습니다: " + sectionId);
        }
        if (request != null) {
            Object sectionTitle = request.get("sectionTitle");
            if (sectionTitle != null) existing.setSectionTitle(sectionTitle.toString());
            Object content = request.get("content");
            if (content != null) existing.setContent(content.toString());
            Object sortOrder = request.get("sortOrder");
            if (sortOrder != null) existing.setSortOrder(Integer.valueOf(sortOrder.toString()));
        }
        sectionMapper.update(existing);
        return sectionMapper.findById(sectionId);
    }

    /**
     * 섹션 내용만 수정 (자동저장용 — content만 업데이트)
     */
    public PbsPageSection updateSectionContent(Long sectionId, Map<String, Object> request) {
        PbsPageSection existing = sectionMapper.findById(sectionId);
        if (existing == null) {
            throw new IllegalArgumentException("섹션을 찾을 수 없습니다: " + sectionId);
        }
        if (request == null) {
            throw new IllegalArgumentException("요청 본문은 필수입니다.");
        }
        Object content = request.get("content");
        if (content == null) {
            throw new IllegalArgumentException("content는 필수입니다.");
        }
        sectionMapper.updateContent(sectionId, content.toString());
        return sectionMapper.findById(sectionId);
    }

    /**
     * 섹션 삭제
     */
    public void deleteSection(Long sectionId) {
        PbsPageSection existing = sectionMapper.findById(sectionId);
        if (existing == null) {
            throw new IllegalArgumentException("섹션을 찾을 수 없습니다: " + sectionId);
        }
        sectionMapper.deleteById(sectionId);
    }

    /**
     * 섹션 재정렬 (sort_order 일괄 업데이트)
     * request 맵: sections → [{sectionId, sortOrder}, ...]
     */
    public void reorderSections(Map<String, Object> request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 본문은 필수입니다.");
        }
        Object sectionsObj = request.get("sections");
        if (sectionsObj == null) {
            throw new IllegalArgumentException("sections는 필수입니다.");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sections = (List<Map<String, Object>>) sectionsObj;
        for (Map<String, Object> item : sections) {
            Object sectionId = item.get("sectionId");
            Object sortOrder = item.get("sortOrder");
            if (sectionId != null && sortOrder != null) {
                sectionMapper.updateSortOrder(
                        Long.valueOf(sectionId.toString()),
                        Integer.valueOf(sortOrder.toString())
                );
            }
        }
    }
}
