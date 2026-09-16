package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.Menu;

import java.util.List;

/**
 * 메뉴 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/MenuMapper.xml
 */
@Mapper
public interface MenuMapper {

    /** 전체 메뉴 조회 (평면 목록) */
    List<Menu> findAll();

    /** 메뉴명/코드/URL 검색 조회 */
    List<Menu> findByKeyword(@Param("keyword") String keyword);

    /** 상위 메뉴 ID로 하위 메뉴 조회 */
    List<Menu> findByParentId(@Param("parentId") Long parentId);

    /** 1Depth(루트) 메뉴 조회 */
    List<Menu> findRootMenus();

    /** 메뉴 ID로 조회 */
    Menu findById(@Param("menuId") Long menuId);

    /** 메뉴 등록 */
    int insert(Menu menu);

    /** 메뉴 수정 */
    int update(Menu menu);

    /** 메뉴 삭제 */
    int deleteById(@Param("menuId") Long menuId);

    /** 총 메뉴 수 */
    int count();
}