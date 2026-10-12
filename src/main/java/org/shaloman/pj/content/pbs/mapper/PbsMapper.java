package org.shaloman.pj.content.pbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.content.pbs.domain.Pbs;

import java.util.List;

/**
 * PBS MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/PbsMapper.xml
 */
@Mapper
public interface PbsMapper {

    /** 전체 PBS 조회 */
    List<Pbs> findAll();

    /** PBS ID로 조회 */
    Pbs findById(@Param("pbsId") Long pbsId);

    /** PBS 등록 */
    int insert(Pbs pbs);

    /** PBS 수정 */
    int update(Pbs pbs);

    /** PBS ID로 삭제 */
    int deleteById(@Param("pbsId") Long pbsId);
}