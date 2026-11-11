package org.shaloman.pj.content.pbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.content.pbs.domain.PbsReference;

import java.util.List;

/**
 * PBS 참조(첨부) MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/PbsReferenceMapper.xml
 */
@Mapper
public interface PbsReferenceMapper {

    /** 페이지 ID로 참조 목록 조회 */
    List<PbsReference> findByPageId(@Param("pageId") Long pageId);

    /** 참조 등록 */
    int insert(PbsReference reference);

    /** 참조 ID로 삭제 */
    int deleteById(@Param("refId") Long refId);

    /** 페이지 ID로 모든 참조 삭제 */
    int deleteByPageId(@Param("pageId") Long pageId);
}
