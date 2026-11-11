package org.shaloman.pj.content.pbs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.content.pbs.domain.PbsPage;
import org.shaloman.pj.content.pbs.service.PbsPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PBS 페이지 RESTful API 컨트롤러.
 * Confluence 스타일 페이지 CRUD, 자동저장, 배포 API.
 */
@RestController
@RequestMapping("/api/pbs/pages")
@Tag(name = "PBS 페이지 관리", description = "Confluence 스타일 페이지 CRUD + 자동저장 + 배포 API")
public class PbsPageController {

    private final PbsPageService pageService;

    public PbsPageController(PbsPageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/{pageId}")
    @Operation(summary = "페이지 상세 조회", description = "페이지 ID로 단일 페이지 정보를 조회한다 (참조 포함).")
    public ResponseEntity<PbsPage> getPageById(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId) {
        return ResponseEntity.ok(pageService.getPageById(pageId));
    }

    @PostMapping
    @Operation(summary = "임시 페이지 생성", description = "folder_id로 임시 제목('새 페이지')의 DRAFT 페이지를 생성한다. folderId 필수.")
    public ResponseEntity<PbsPage> createPage(@RequestBody Map<String, Object> request) {
        PbsPage created = pageService.createPage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/reorder")
    @Operation(summary = "페이지 순서 변경 (drag&drop)", description = "페이지들의 정렬 순서를 일괄 변경한다. folderId가 포함된 항목은 다른 폴더로 이동. body: [{pageId, sortOrder, folderId?}, ...]")
    public ResponseEntity<Void> reorderPages(@RequestBody List<Map<String, Object>> items) {
        pageService.reorderPages(items);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{pageId}/content")
    @Operation(summary = "본문 자동저장", description = "페이지의 본문 내용(content)만 업데이트한다. 에디터 자동저장용.")
    public ResponseEntity<PbsPage> updateContent(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(pageService.updateContent(pageId, request));
    }

    @PutMapping("/{pageId}/publish")
    @Operation(summary = "페이지 배포", description = "페이지 상태를 PUBLISHED로 변경하고 published_at을 설정한다.")
    public ResponseEntity<PbsPage> publishPage(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId) {
        return ResponseEntity.ok(pageService.publishPage(pageId));
    }

    @PutMapping("/{pageId}")
    @Operation(summary = "페이지 전체 수정", description = "페이지의 모든 필드(제목, 본문, 성경 범위, 상태 등)를 수정한다.")
    public ResponseEntity<PbsPage> updatePage(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(pageService.updatePage(pageId, request));
    }

    @DeleteMapping("/{pageId}")
    @Operation(summary = "페이지 삭제", description = "페이지 ID로 페이지를 삭제한다 (참조도 함께 삭제).")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePage(
            @Parameter(description = "페이지 ID", example = "1")
            @PathVariable Long pageId) {
        pageService.deletePage(pageId);
    }
}
