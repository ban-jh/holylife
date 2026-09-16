package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.Role;

import java.util.List;

/**
 * 권한 그룹 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/RoleMapper.xml
 */
@Mapper
public interface RoleMapper {

    /** 전체 권한 그룹 조회 */
    List<Role> findAll();

    /** 그룹명/그룹코드/설명 검색 조회 */
    List<Role> findByKeyword(@Param("keyword") String keyword);

    /** 사용 여부로 권한 그룹 조회 */
    List<Role> findByUseYn(@Param("useYn") Boolean useYn);

    /** 권한 그룹 ID로 조회 */
    Role findById(@Param("roleId") Long roleId);

    /** 그룹 코드로 조회 */
    Role findByGroupCode(@Param("groupCode") String groupCode);

    /** 권한 그룹 등록 */
    int insert(Role role);

    /** 권한 그룹 수정 */
    int update(Role role);

    /** 권한 그룹 삭제 */
    int deleteById(@Param("roleId") Long roleId);

    /** 총 권한 그룹 수 */
    int count();
}