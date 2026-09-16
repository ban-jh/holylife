package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.shaloman.pj.dto.MenuPermissionResponseDto;
import org.shaloman.pj.dto.PermissionRequestDto;
import org.shaloman.pj.dto.PermissionResponseDto;
import org.shaloman.pj.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 메뉴 권한 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "메뉴 권한 관리", description = "role-menu 권한 매핑 CRUD 및 일괄 업데이트 API")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @Operation(summary = "전체 권한 조회", description = "등록된 모든 권한 목록을 조회한다. roleId 파라미터 시 해당 role의 권한만 조회한다.")
    public ResponseEntity<List<PermissionResponseDto>> getAllPermissions(
            @Parameter(description = "권한 그룹 ID") @RequestParam(required = false) Long roleId) {
        if (roleId != null) {
            return ResponseEntity.ok(permissionService.getPermissionsByRoleId(roleId));
        }
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/role/{roleId}/menus")
    @Operation(summary = "role_id별 메뉴 권한 트리 조회", description = "특정 role_id에 대한 전체 메뉴 트리 + 체크(권한) 상태를 반환한다.")
    public ResponseEntity<List<MenuPermissionResponseDto>> getMenuPermissionTreeByRoleId(
            @Parameter(description = "권한 그룹 ID", example = "1")
            @PathVariable Long roleId) {
        return ResponseEntity.ok(permissionService.getMenuPermissionTreeByRoleId(roleId));
    }

    @GetMapping("/{permissionId}")
    @Operation(summary = "권한 상세 조회", description = "권한 ID로 단일 권한 정보를 조회한다.")
    public ResponseEntity<PermissionResponseDto> getPermissionById(
            @Parameter(description = "권한 ID", example = "1")
            @PathVariable Long permissionId) {
        return ResponseEntity.ok(permissionService.getPermissionById(permissionId));
    }

    @PostMapping
    @Operation(summary = "권한 등록", description = "새로운 role-menu 권한 매핑을 등록한다.")
    public ResponseEntity<PermissionResponseDto> createPermission(@Valid @RequestBody PermissionRequestDto request) {
        PermissionResponseDto created = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{permissionId}")
    @Operation(summary = "권한 수정", description = "기존 권한 정보를 수정한다.")
    public ResponseEntity<PermissionResponseDto> updatePermission(
            @Parameter(description = "권한 ID", example = "1")
            @PathVariable Long permissionId,
            @Valid @RequestBody PermissionRequestDto request) {
        return ResponseEntity.ok(permissionService.updatePermission(permissionId, request));
    }

    @PutMapping("/role/{roleId}")
    @Operation(summary = "role_id별 권한 일괄 업데이트", description = "특정 role_id의 권한 매핑을 일괄 업데이트한다 (기존 권한 삭제 후 재등록).")
    public ResponseEntity<Void> bulkUpdatePermissionsByRoleId(
            @Parameter(description = "권한 그룹 ID", example = "1")
            @PathVariable Long roleId,
            @RequestBody List<PermissionRequestDto> requests) {
        permissionService.bulkUpdatePermissionsByRoleId(roleId, requests);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{permissionId}")
    @Operation(summary = "권한 삭제", description = "권한 ID로 권한 매핑을 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(
            @Parameter(description = "권한 ID", example = "1")
            @PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
    }
}