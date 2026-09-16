package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.Permission;

import java.util.List;

/**
 * 권한 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/PermissionMapper.xml
 */
@Mapper
public interface PermissionMapper {

    /** 전체 권한 조회 */
    List<Permission> findAll();

    /** 권한 그룹 ID로 권한 목록 조회 */
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /** 권한 ID로 조회 */
    Permission findById(@Param("permissionId") Long permissionId);

    /** role_id + menu_id로 권한 조회 */
    Permission findByRoleAndMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    /** 권한 등록 */
    int insert(Permission permission);

    /** 권한 수정 */
    int update(Permission permission);

    /** 권한 삭제 */
    int deleteById(@Param("permissionId") Long permissionId);

    /** 특정 role_id의 권한 전체 삭제 (bulk update 시 사용) */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /** 총 권한 수 */
    int count();
}