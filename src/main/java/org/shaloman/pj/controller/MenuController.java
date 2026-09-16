package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.shaloman.pj.dto.MenuRequestDto;
import org.shaloman.pj.dto.MenuResponseDto;
import org.shaloman.pj.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 메뉴 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/menus")
@Tag(name = "메뉴 관리", description = "메뉴 CRUD 및 트리 조회 API")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @Operation(summary = "전체 메뉴 조회", description = "등록된 모든 메뉴 목록을 조회한다. keyword 파라미터 시 검색한다.")
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(
            @Parameter(description = "검색어 (메뉴명/코드/URL)") @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(menuService.searchMenus(keyword));
        }
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/tree")
    @Operation(summary = "메뉴 트리 조회", description = "계층형 메뉴 트리를 조회한다 (parent-children 구조).")
    public ResponseEntity<List<MenuResponseDto>> getMenuTree() {
        return ResponseEntity.ok(menuService.getMenuTree());
    }

    @GetMapping("/{menuId}/children")
    @Operation(summary = "하위 메뉴 조회", description = "특정 상위 메뉴 ID의 하위 메뉴 목록을 조회한다.")
    public ResponseEntity<List<MenuResponseDto>> getChildMenus(
            @Parameter(description = "상위 메뉴 ID", example = "1")
            @PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getChildMenus(menuId));
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회", description = "메뉴 ID로 단일 메뉴 정보를 조회한다.")
    public ResponseEntity<MenuResponseDto> getMenuById(
            @Parameter(description = "메뉴 ID", example = "1")
            @PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getMenuById(menuId));
    }

    @PostMapping
    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록한다.")
    public ResponseEntity<MenuResponseDto> createMenu(@Valid @RequestBody MenuRequestDto request) {
        MenuResponseDto created = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "기존 메뉴 정보를 수정한다.")
    public ResponseEntity<MenuResponseDto> updateMenu(
            @Parameter(description = "메뉴 ID", example = "1")
            @PathVariable Long menuId,
            @Valid @RequestBody MenuRequestDto request) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, request));
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제", description = "메뉴 ID로 메뉴를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(
            @Parameter(description = "메뉴 ID", example = "1")
            @PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
    }
}