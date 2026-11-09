package org.shaloman.pj.content.bible.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.content.bible.domain.BibleBook;
import org.shaloman.pj.content.bible.mapper.BibleBookMapper;
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
    @Operation(summary = "성경 구절 조회", description = "장 범위(chapterFrom~chapterTo)와 절 범위(verseFrom~verseTo)로 구절을 조회한다. chapterTo 미지정 시 단일 장, verseFrom/verseTo 미지정 시 전체 장.")
    public ResponseEntity<List<BibleBook>> getVerses(
            @Parameter(description = "책 코드", example = "Gen", required = true)
            @RequestParam String bookCode,
            @Parameter(description = "시작 장", example = "1", required = true)
            @RequestParam Integer chapterFrom,
            @Parameter(description = "시작 절 (선택)", example = "20")
            @RequestParam(required = false) Integer verseFrom,
            @Parameter(description = "종료 장 (선택, 미지정 시 chapterFrom 단일 장)", example = "2")
            @RequestParam(required = false) Integer chapterTo,
            @Parameter(description = "종료 절 (선택)", example = "5")
            @RequestParam(required = false) Integer verseTo,
            @Parameter(description = "번역본 ID (기본값 2 = 개역개정)", example = "2")
            @RequestParam(defaultValue = "2") Long versionId) {

        List<BibleBook> result = bibleBookMapper.findVerses(
                versionId, bookCode, chapterFrom, verseFrom, chapterTo, verseTo);
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