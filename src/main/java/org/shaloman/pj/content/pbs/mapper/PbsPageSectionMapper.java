package org.shaloman.pj.content.pbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.content.pbs.domain.PbsPageSection;

import java.util.List;

/**
 * PBS 페이지 섹션 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/PbsPageSectionMapper.xml
 */
@Mapper
public interface PbsPageSectionMapper {

    /** 페이지 ID로 섹션 목록 조회 (sort_order ASC) */
    List<PbsPageSection> findByPageId(@Param("pageId") Long pageId);

    /** 섹션 ID로 단일 조회 */
    PbsPageSection findById(@Param("sectionId") Long sectionId);

    /** 섹션 등록 */
    int insert(PbsPageSection section);

    /** 섹션 전체 수정 (제목, 내용, 정렬순서) */
    int update(PbsPageSection section);

    /** 섹션 내용만 수정 (자동저장용) */
    int updateContent(@Param("sectionId") Long sectionId, @Param("content") String content);

    /** 섹션 ID로 삭제 */
    int deleteById(@Param("sectionId") Long sectionId);

    /** 페이지 ID로 모든 섹션 삭제 */
    int deleteByPageId(@Param("pageId") Long pageId);

    /** 섹션 정렬순서 업데이트 (reorder용) */
    int updateSortOrder(@Param("sectionId") Long sectionId, @Param("sortOrder") Integer sortOrder);
}
