package org.shaloman.pj.content.pbs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.content.pbs.domain.PbsPageSection;
import org.shaloman.pj.content.pbs.service.PbsPageSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PBS 페이지 섹션 RESTful API 컨트롤러.
 * 영역별 페이징(관찰/해석/적용 등) 섹션 CRUD + 자동저장 + 재정렬 API.
 */
@RestController
@RequestMapping("/api/pbs")
@Tag(name = "PBS 페이지 섹션 관리", description = "영역별 페이징 섹션 CRUD + 자동저장 + 재정렬 API")
public class PbsPageSectionController {

    private final PbsPageSectionService sectionService;

    public PbsPageSectionController(PbsPageSectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/pages/{pageId}/sections")
    @Operation(summary = "페이지 섹션 목록 조회", description = "페이지 ID로 섹션 목록을 조회한다 (sort_order ASC).")
    public ResponseEntity<List<PbsPageSection>> getSections(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId) {
        return ResponseEntity.ok(sectionService.getSectionsByPageId(pageId));
    }

    @PostMapping("/pages/{pageId}/sections")
    @Operation(summary = "섹션 추가", description = "페이지에 새 섹션을 추가한다. sectionTitle 필수, content/sortOrder optional.")
    public ResponseEntity<PbsPageSection> createSection(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId,
            @RequestBody Map<String, Object> request) {
        PbsPageSection created = sectionService.createSection(pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/sections/{sectionId}")
    @Operation(summary = "섹션 수정", description = "섹션의 제목, 내용, 정렬순서를 수정한다.")
    public ResponseEntity<PbsPageSection> updateSection(
            @Parameter(description = "섹션 ID", example = "1")
            @PathVariable Long sectionId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(sectionService.updateSection(sectionId, request));
    }

    @PutMapping("/sections/{sectionId}/content")
    @Operation(summary = "섹션 내용 자동저장", description = "섹션의 내용(content)만 업데이트한다. 에디터 자동저장용.")
    public ResponseEntity<PbsPageSection> updateSectionContent(
            @Parameter(description = "섹션 ID", example = "1")
            @PathVariable Long sectionId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(sectionService.updateSectionContent(sectionId, request));
    }

    @DeleteMapping("/sections/{sectionId}")
    @Operation(summary = "섹션 삭제", description = "섹션 ID로 섹션을 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSection(
            @Parameter(description = "섹션 ID", example = "1")
            @PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);
    }

    @PutMapping("/pages/{pageId}/sections/reorder")
    @Operation(summary = "섹션 재정렬", description = "페이지 내 섹션들의 정렬순서를 일괄 업데이트한다. sections: [{sectionId, sortOrder}, ...]")
    public ResponseEntity<Void> reorderSections(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId,
            @RequestBody Map<String, Object> request) {
        sectionService.reorderSections(request);
        return ResponseEntity.ok().build();
    }
}
