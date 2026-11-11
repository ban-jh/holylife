package org.shaloman.pj.content.pbs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.content.pbs.domain.PbsFolder;
import org.shaloman.pj.content.pbs.service.PbsFolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PBS 폴더 RESTful API 컨트롤러.
 * Confluence 스타일 폴더 트리 관리 API.
 */
@RestController
@RequestMapping("/api/pbs/folders")
@Tag(name = "PBS 폴더 관리", description = "Confluence 스타일 폴더 트리 CRUD API")
public class PbsFolderController {

    private final PbsFolderService folderService;

    public PbsFolderController(PbsFolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    @Operation(summary = "폴더 트리 조회", description = "전체 폴더를 트리 구조(children + pages 포함)로 조회한다.")
    public ResponseEntity<List<PbsFolder>> getFolderTree() {
        return ResponseEntity.ok(folderService.getFolderTree());
    }

    @PostMapping
    @Operation(summary = "폴더 생성", description = "새로운 폴더를 생성한다. folderName 필수, parentId/sortOrder optional.")
    public ResponseEntity<PbsFolder> createFolder(@RequestBody Map<String, Object> request) {
        PbsFolder created = folderService.createFolder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/reorder")
    @Operation(summary = "폴더 순서 변경 (drag&drop)", description = "폴더들의 정렬 순서를 일괄 변경한다. body: [{folderId, sortOrder}, ...]")
    public ResponseEntity<Void> reorderFolders(@RequestBody List<Map<String, Object>> items) {
        folderService.reorderFolders(items);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{folderId}")
    @Operation(summary = "폴더 수정", description = "기존 폴더 정보(이름, 부모, 정렬순서)를 수정한다.")
    public ResponseEntity<PbsFolder> updateFolder(
            @Parameter(description = "폴더 ID", example = "1")
            @PathVariable Long folderId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(folderService.updateFolder(folderId, request));
    }

    @DeleteMapping("/{folderId}")
    @Operation(summary = "폴더 삭제", description = "폴더를 삭제한다. 하위 폴더가 있으면 재귀적으로 함께 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFolder(
            @Parameter(description = "폴더 ID", example = "1")
            @PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
    }
}
