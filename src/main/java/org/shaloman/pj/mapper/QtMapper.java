package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.Qt;

import java.time.LocalDate;
import java.util.List;

/**
 * 큐티(QT) MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/QtMapper.xml
 */
@Mapper
public interface QtMapper {

    /** 전체 큐티 조회 */
    List<Qt> findAll();

    /** 사용자 ID로 큐티 조회 */
    List<Qt> findByUserId(@Param("userId") Long userId);

    /** 큐티 ID로 조회 */
    Qt findById(@Param("qtId") Long qtId);

    /** 사용자 ID + 날짜로 큐티 조회 */
    Qt findByUserIdAndDate(@Param("userId") Long userId, @Param("qtDate") LocalDate qtDate);

    /** 큐티 등록 */
    int insert(Qt qt);

    /** 큐티 수정 */
    int update(Qt qt);

    /** 큐티 ID로 삭제 */
    int deleteById(@Param("qtId") Long qtId);
}