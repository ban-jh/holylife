package org.shaloman.pj.content.pbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.content.pbs.domain.PbsPage;

import java.util.List;

/**
 * PBS 페이지 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/PbsPageMapper.xml
 */
@Mapper
public interface PbsPageMapper {

    /** 전체 페이지 조회 */
    List<PbsPage> findAll();

    /** 페이지 ID로 조회 (참조 포함) */
    PbsPage findById(@Param("pageId") Long pageId);

    /** 폴더 ID로 페이지 목록 조회 */
    List<PbsPage> findByFolderId(@Param("folderId") Long folderId);

    /** 페이지 등록 (임시 제목으로 새 페이지 생성) */
    int insert(PbsPage page);

    /** 페이지 전체 수정 (동적 SET) */
    int update(PbsPage page);

    /** 페이지 본문 내용 수정 (자동저장용) */
    int updateContent(@Param("pageId") Long pageId, @Param("content") String content);

    /** 페이지 상태 변경 (배포용: DRAFT → PUBLISHED, published_at 설정) */
    int updateStatus(@Param("pageId") Long pageId, @Param("status") String status);

    /** 페이지 ID로 삭제 */
    int deleteById(@Param("pageId") Long pageId);
}
