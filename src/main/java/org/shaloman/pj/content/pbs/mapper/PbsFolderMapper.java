package org.shaloman.pj.content.pbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.content.pbs.domain.PbsFolder;

import java.util.List;

/**
 * PBS 폴더 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/PbsFolderMapper.xml
 */
@Mapper
public interface PbsFolderMapper {

    /** 전체 폴더 조회 (정렬순 오름차순) */
    List<PbsFolder> findAll();

    /** 폴더 ID로 조회 */
    PbsFolder findById(@Param("folderId") Long folderId);

    /** 하위 폴더 조회 (특정 부모의 자식 폴더들) */
    List<PbsFolder> findByParentId(@Param("parentId") Long parentId);

    /** 폴더+하위 페이지 트리 조회 (모든 폴더와 각 폴더의 페이지를 조인) */
    List<PbsFolder> findTree();

    /** 폴더 등록 */
    int insert(PbsFolder folder);

    /** 폴더 수정 (동적 SET) */
    int update(PbsFolder folder);

    /** 폴더 정렬순서 변경 (drag&drop) */
    int updateSortOrder(@Param("folderId") Long folderId, @Param("sortOrder") Integer sortOrder);

    /** 폴더 ID로 삭제 */
    int deleteById(@Param("folderId") Long folderId);
}
