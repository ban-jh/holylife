package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.Sermon;

import java.util.List;

/**
 * 설교 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/SermonMapper.xml
 */
@Mapper
public interface SermonMapper {

    /** 전체 설교 조회 */
    List<Sermon> findAll();

    /** 설교 ID로 조회 */
    Sermon findById(@Param("sermonId") Long sermonId);

    /** 설교 등록 */
    int insert(Sermon sermon);

    /** 설교 수정 */
    int update(Sermon sermon);

    /** 설교 ID로 삭제 */
    int deleteById(@Param("sermonId") Long sermonId);
}