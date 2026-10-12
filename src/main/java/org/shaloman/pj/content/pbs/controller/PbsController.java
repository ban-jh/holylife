package org.shaloman.pj.content.pbs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.content.pbs.domain.Pbs;
import org.shaloman.pj.content.pbs.service.PbsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PBS (P.B.S Bible Study) RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/pbs")
@Tag(name = "PBS 관리", description = "PBS (성경 공부) CRUD API")
public class PbsController {

    private final PbsService pbsService;

    public PbsController(PbsService pbsService) {
        this.pbsService = pbsService;
    }

    @GetMapping
    @Operation(summary = "전체 PBS 조회", description = "등록된 모든 PBS 목록을 조회한다 (PBS 날짜 내림차순).")
    public ResponseEntity<List<Pbs>> getAllPbs() {
        return ResponseEntity.ok(pbsService.getAllPbs());
    }

    @GetMapping("/{pbsId}")
    @Operation(summary = "PBS 상세 조회", description = "PBS ID로 단일 PBS 정보를 조회한다.")
    public ResponseEntity<Pbs> getPbsById(
            @Parameter(description = "PBS ID", example = "1")
            @PathVariable Long pbsId) {
        Pbs pbs = pbsService.getPbsById(pbsId);
        if (pbs == null) {
            throw new IllegalArgumentException("PBS를 찾을 수 없습니다: " + pbsId);
        }
        return ResponseEntity.ok(pbs);
    }

    @PostMapping
    @Operation(summary = "PBS 등록", description = "새로운 PBS를 등록한다. userId=1(관리자), status=DRAFT로 설정.")
    public ResponseEntity<Pbs> createPbs(@RequestBody Map<String, Object> request) {
        Pbs created = pbsService.createPbs(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{pbsId}")
    @Operation(summary = "PBS 수정", description = "기존 PBS 정보를 수정한다.")
    public ResponseEntity<Pbs> updatePbs(
            @Parameter(description = "PBS ID", example = "1")
            @PathVariable Long pbsId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(pbsService.updatePbs(pbsId, request));
    }

    @DeleteMapping("/{pbsId}")
    @Operation(summary = "PBS 삭제", description = "PBS ID로 PBS를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePbs(
            @Parameter(description = "PBS ID", example = "1")
            @PathVariable Long pbsId) {
        pbsService.deletePbs(pbsId);
    }
}