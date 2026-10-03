package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.domain.Qt;
import org.shaloman.pj.service.QtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 큐티(QT) RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/qts")
@Tag(name = "QT 관리", description = "QT CRUD API")
public class QtController {

    private final QtService qtService;

    public QtController(QtService qtService) {
        this.qtService = qtService;
    }

    @GetMapping
    @Operation(summary = "전체 QT 조회", description = "등록된 모든 QT 목록을 조회한다 (큐티 날짜 내림차순).")
    public ResponseEntity<List<Qt>> getAllQts() {
        return ResponseEntity.ok(qtService.getAllQts());
    }

    @GetMapping("/{qtId}")
    @Operation(summary = "QT 상세 조회", description = "QT ID로 단일 QT 정보를 조회한다.")
    public ResponseEntity<Qt> getQtById(
            @Parameter(description = "QT ID", example = "1")
            @PathVariable Long qtId) {
        Qt qt = qtService.getQtById(qtId);
        if (qt == null) {
            throw new IllegalArgumentException("QT를 찾을 수 없습니다: " + qtId);
        }
        return ResponseEntity.ok(qt);
    }

    @PostMapping
    @Operation(summary = "QT 등록", description = "새로운 QT를 등록한다. userId는 요청에서 가져오거나 기본값 1, visibility는 기본값 PUBLIC.")
    public ResponseEntity<Qt> createQt(@RequestBody Map<String, Object> request) {
        Qt created = qtService.createQt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{qtId}")
    @Operation(summary = "QT 수정", description = "기존 QT 정보를 수정한다.")
    public ResponseEntity<Qt> updateQt(
            @Parameter(description = "QT ID", example = "1")
            @PathVariable Long qtId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(qtService.updateQt(qtId, request));
    }

    @DeleteMapping("/{qtId}")
    @Operation(summary = "QT 삭제", description = "QT ID로 QT를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQt(
            @Parameter(description = "QT ID", example = "1")
            @PathVariable Long qtId) {
        qtService.deleteQt(qtId);
    }
}