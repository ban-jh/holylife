package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.domain.Sermon;
import org.shaloman.pj.service.SermonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 설교 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/sermons")
@Tag(name = "설교 관리", description = "설교 CRUD API")
public class SermonController {

    private final SermonService sermonService;

    public SermonController(SermonService sermonService) {
        this.sermonService = sermonService;
    }

    @GetMapping
    @Operation(summary = "전체 설교 조회", description = "등록된 모든 설교 목록을 조회한다 (설교 날짜 내림차순).")
    public ResponseEntity<List<Sermon>> getAllSermons() {
        return ResponseEntity.ok(sermonService.getAllSermons());
    }

    @GetMapping("/{sermonId}")
    @Operation(summary = "설교 상세 조회", description = "설교 ID로 단일 설교 정보를 조회한다.")
    public ResponseEntity<Sermon> getSermonById(
            @Parameter(description = "설교 ID", example = "1")
            @PathVariable Long sermonId) {
        Sermon sermon = sermonService.getSermonById(sermonId);
        if (sermon == null) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        return ResponseEntity.ok(sermon);
    }

    @PostMapping
    @Operation(summary = "설교 등록", description = "새로운 설교를 등록한다. userId=1(관리자), status=DRAFT로 설정.")
    public ResponseEntity<Sermon> createSermon(@RequestBody Map<String, Object> request) {
        Sermon created = sermonService.createSermon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{sermonId}")
    @Operation(summary = "설교 수정", description = "기존 설교 정보를 수정한다.")
    public ResponseEntity<Sermon> updateSermon(
            @Parameter(description = "설교 ID", example = "1")
            @PathVariable Long sermonId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(sermonService.updateSermon(sermonId, request));
    }

    @DeleteMapping("/{sermonId}")
    @Operation(summary = "설교 삭제", description = "설교 ID로 설교를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSermon(
            @Parameter(description = "설교 ID", example = "1")
            @PathVariable Long sermonId) {
        sermonService.deleteSermon(sermonId);
    }
}