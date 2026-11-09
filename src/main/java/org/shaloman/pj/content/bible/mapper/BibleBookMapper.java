package org.shaloman.pj.content.bible.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.content.bible.domain.BibleBook;

import java.util.List;

/**
 * 성경 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/BibleBookMapper.xml
 */
@Mapper
public interface BibleBookMapper {

    /** 장 범위 조회 (chapterFrom~chapterTo, 절 범위 포함) */
    List<BibleBook> findVerses(@Param("versionId") Long versionId,
                               @Param("bookCode") String bookCode,
                               @Param("chapterFrom") Integer chapterFrom,
                               @Param("verseFrom") Integer verseFrom,
                               @Param("chapterTo") Integer chapterTo,
                               @Param("verseTo") Integer verseTo);

    /** 특정 장 전체 조회 */
    List<BibleBook> findChapter(@Param("versionId") Long versionId,
                                @Param("bookCode") String bookCode,
                                @Param("chapter") Integer chapter);

    /** 책 목록 조회 (DISTINCT book_code, book_name_kr, testament) */
    List<BibleBook> findBookList(@Param("versionId") Long versionId);

    /** 책명(한글)으로 조회 (LIMIT 1) */
    BibleBook findByBookNameKr(@Param("versionId") Long versionId,
                               @Param("bookNameKr") String bookNameKr);
}