package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.domain.BibleBook;
import org.shaloman.pj.mapper.BibleBookMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 성경 조회 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/bible")
@Tag(name = "성경", description = "성경 구절 및 책 목록 조회 API")
public class BibleController {

    private final BibleBookMapper bibleBookMapper;

    public BibleController(BibleBookMapper bibleBookMapper) {
        this.bibleBookMapper = bibleBookMapper;
    }

    @GetMapping("/verses")
    @Operation(summary = "성경 구절 조회", description = "특정 책/장의 구절을 조회한다. verseFrom/verseTo 지정 시 해당 범위, 미지정 시 전체 장을 반환한다.")
    public ResponseEntity<List<BibleBook>> getVerses(
            @Parameter(description = "책 코드", example = "Gen", required = true)
            @RequestParam String bookCode,
            @Parameter(description = "장", example = "1", required = true)
            @RequestParam Integer chapter,
            @Parameter(description = "시작 절 (선택)", example = "1")
            @RequestParam(required = false) Integer verseFrom,
            @Parameter(description = "종료 절 (선택)", example = "3")
            @RequestParam(required = false) Integer verseTo,
            @Parameter(description = "번역본 ID (기본값 2 = 개역개정)", example = "2")
            @RequestParam(defaultValue = "2") Long versionId) {

        List<BibleBook> result;
        if (verseFrom != null && verseTo != null) {
            result = bibleBookMapper.findVerses(versionId, bookCode, chapter, verseFrom, verseTo);
        } else {
            result = bibleBookMapper.findChapter(versionId, bookCode, chapter);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/books")
    @Operation(summary = "성경 책 목록 조회", description = "해당 번역본의 전체 책 목록을 조회한다.")
    public ResponseEntity<List<BibleBook>> getBooks(
            @Parameter(description = "번역본 ID (기본값 2 = 개역개정)", example = "2")
            @RequestParam(defaultValue = "2") Long versionId) {
        return ResponseEntity.ok(bibleBookMapper.findBookList(versionId));
    }
}