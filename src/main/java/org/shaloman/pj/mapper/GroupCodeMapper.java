package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.GroupCode;

import java.util.List;

/**
 * 그룹 코드 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/GroupCodeMapper.xml
 */
@Mapper
public interface GroupCodeMapper {

    /** 전체 그룹 코드 조회 */
    List<GroupCode> findAll();

    /** 그룹 코드명/코드 검색 조회 */
    List<GroupCode> findByKeyword(@Param("keyword") String keyword);

    /** 그룹 코드로 조회 */
    GroupCode findById(@Param("groupCode") String groupCode);

    /** 그룹 코드 등록 */
    int insert(GroupCode groupCode);

    /** 그룹 코드 수정 */
    int update(GroupCode groupCode);

    /** 그룹 코드 삭제 */
    int deleteById(@Param("groupCode") String groupCode);

    /** 총 그룹 코드 수 */
    int count();
}