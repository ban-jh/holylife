package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.CommonCode;

import java.util.List;

/**
 * 공통 코드 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/CommonCodeMapper.xml
 */
@Mapper
public interface CommonCodeMapper {

    /** 전체 공통 코드 조회 */
    List<CommonCode> findAll();

    /** 그룹 코드에 해당하는 공통 코드 목록 조회 */
    List<CommonCode> findByGroupCode(@Param("groupCode") String groupCode);

    /** 그룹 코드 내에서 코드/코드명 검색 조회 */
    List<CommonCode> findByGroupCodeAndKeyword(@Param("groupCode") String groupCode,
                                               @Param("keyword") String keyword);

    /** 코드 ID로 조회 */
    CommonCode findById(@Param("codeId") Long codeId);

    /** 코드 등록 */
    int insert(CommonCode commonCode);

    /** 코드 수정 */
    int update(CommonCode commonCode);

    /** 코드 삭제 */
    int deleteById(@Param("codeId") Long codeId);

    /** 특정 그룹 코드에 속한 코드 전체 삭제 (그룹 삭제 시) */
    int deleteByGroupCode(@Param("groupCode") String groupCode);

    /** 총 코드 수 */
    int count();
}