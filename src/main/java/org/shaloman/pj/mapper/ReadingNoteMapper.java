package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.ReadingNote;

import java.util.List;

/**
 * 독서 노트 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/ReadingNoteMapper.xml
 */
@Mapper
public interface ReadingNoteMapper {

    /** 전체 독서 노트 조회 */
    List<ReadingNote> findAll();

    /** 독서 노트 ID로 조회 */
    ReadingNote findById(@Param("readingId") Long readingId);

    /** 독서 노트 등록 */
    int insert(ReadingNote readingNote);

    /** 독서 노트 수정 */
    int update(ReadingNote readingNote);

    /** 독서 노트 ID로 삭제 */
    int deleteById(@Param("readingId") Long readingId);
}