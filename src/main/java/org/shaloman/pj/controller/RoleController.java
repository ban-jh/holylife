package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.shaloman.pj.dto.RoleRequestDto;
import org.shaloman.pj.dto.RoleResponseDto;
import org.shaloman.pj.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 권한 그룹 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "권한 그룹 관리", description = "권한 그룹(roles) CRUD API")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "전체 권한 그룹 조회", description = "등록된 모든 권한 그룹 목록을 조회한다. keyword 파라미터 시 검색, useYn 파라미터 시 사용여부 필터링한다.")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles(
            @Parameter(description = "검색어 (그룹명/그룹코드/설명)") @RequestParam(required = false) String keyword,
            @Parameter(description = "사용 여부 필터 (true/false)") @RequestParam(required = false) Boolean useYn) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(roleService.searchRoles(keyword));
        }
        if (useYn != null) {
            return ResponseEntity.ok(roleService.getRolesByUseYn(useYn));
        }
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "권한 그룹 상세 조회", description = "권한 그룹 ID로 단일 권한 그룹 정보를 조회한다.")
    public ResponseEntity<RoleResponseDto> getRoleById(
            @Parameter(description = "권한 그룹 ID", example = "1")
            @PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    @PostMapping
    @Operation(summary = "권한 그룹 등록", description = "새로운 권한 그룹을 등록한다.")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto request) {
        RoleResponseDto created = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "권한 그룹 수정", description = "기존 권한 그룹 정보를 수정한다.")
    public ResponseEntity<RoleResponseDto> updateRole(
            @Parameter(description = "권한 그룹 ID", example = "1")
            @PathVariable Long roleId,
            @Valid @RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(roleService.updateRole(roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "권한 그룹 삭제", description = "권한 그룹 ID로 권한 그룹을 삭제한다. 연결된 user_roles, permissions도 자동 삭제된다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(
            @Parameter(description = "권한 그룹 ID", example = "1")
            @PathVariable Long roleId) {
        roleService.deleteRole(roleId);
    }
}