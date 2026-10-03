package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.QtDaily;

import java.time.LocalDate;
import java.util.List;

/**
 * 큐티(QT) 일일 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/QtDailyMapper.xml
 */
@Mapper
public interface QtDailyMapper {

    /** 큐티 upsert (INSERT ON DUPLICATE KEY UPDATE) */
    int upsert(QtDaily qtDaily);

    /** 날짜로 큐티 조회 */
    QtDaily findByDate(@Param("qtDate") LocalDate qtDate);

    /** 날짜+출처로 큐티 조회 */
    QtDaily findByDateAndSource(@Param("qtDate") LocalDate qtDate, @Param("source") String source);

    /** 전체 큐티 조회 */
    List<QtDaily> findAll();

    /** 날짜로 모든 출처의 큐티 조회 (List) */
    List<QtDaily> findAllByDate(@Param("qtDate") LocalDate qtDate);
}