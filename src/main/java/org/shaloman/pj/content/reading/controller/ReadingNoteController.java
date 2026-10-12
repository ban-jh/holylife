package org.shaloman.pj.content.reading.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.content.reading.domain.ReadingNote;
import org.shaloman.pj.content.reading.service.ReadingNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 독서 노트 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/readings")
@Tag(name = "독서 노트 관리", description = "독서 노트 CRUD API")
public class ReadingNoteController {

    private final ReadingNoteService readingNoteService;

    public ReadingNoteController(ReadingNoteService readingNoteService) {
        this.readingNoteService = readingNoteService;
    }

    @GetMapping
    @Operation(summary = "전체 독서 노트 조회", description = "등록된 모든 독서 노트 목록을 조회한다 (독서 날짜 내림차순).")
    public ResponseEntity<List<ReadingNote>> getAllReadingNotes() {
        return ResponseEntity.ok(readingNoteService.getAllReadingNotes());
    }

    @GetMapping("/{readingId}")
    @Operation(summary = "독서 노트 상세 조회", description = "독서 노트 ID로 단일 독서 노트 정보를 조회한다.")
    public ResponseEntity<ReadingNote> getReadingNoteById(
            @Parameter(description = "독서 노트 ID", example = "1")
            @PathVariable Long readingId) {
        ReadingNote readingNote = readingNoteService.getReadingNoteById(readingId);
        if (readingNote == null) {
            throw new IllegalArgumentException("독서 노트를 찾을 수 없습니다: " + readingId);
        }
        return ResponseEntity.ok(readingNote);
    }

    @PostMapping
    @Operation(summary = "독서 노트 등록", description = "새로운 독서 노트를 등록한다. userId=1(관리자)로 설정.")
    public ResponseEntity<ReadingNote> createReadingNote(@RequestBody Map<String, Object> request) {
        ReadingNote created = readingNoteService.createReadingNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{readingId}")
    @Operation(summary = "독서 노트 수정", description = "기존 독서 노트 정보를 수정한다.")
    public ResponseEntity<ReadingNote> updateReadingNote(
            @Parameter(description = "독서 노트 ID", example = "1")
            @PathVariable Long readingId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(readingNoteService.updateReadingNote(readingId, request));
    }

    @DeleteMapping("/{readingId}")
    @Operation(summary = "독서 노트 삭제", description = "독서 노트 ID로 독서 노트를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReadingNote(
            @Parameter(description = "독서 노트 ID", example = "1")
            @PathVariable Long readingId) {
        readingNoteService.deleteReadingNote(readingId);
    }
}