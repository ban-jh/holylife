package org.shaloman.pj.service;

import org.shaloman.pj.domain.Menu;
import org.shaloman.pj.dto.MenuRequestDto;
import org.shaloman.pj.dto.MenuResponseDto;
import org.shaloman.pj.mapper.MenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 메뉴 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class MenuService {

    private final MenuMapper menuMapper;

    public MenuService(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    /**
     * 전체 메뉴 조회 (평면 목록, 읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<MenuResponseDto> getAllMenus() {
        return menuMapper.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 검색 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<MenuResponseDto> searchMenus(String keyword) {
        return menuMapper.findByKeyword(keyword).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 트리 조회 (계층형, 읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<MenuResponseDto> getMenuTree() {
        List<Menu> allMenus = menuMapper.findAll();
        return buildTree(allMenus);
    }

    /**
     * 상위 메뉴 ID로 하위 메뉴 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<MenuResponseDto> getChildMenus(Long parentId) {
        return menuMapper.findByParentId(parentId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 상세 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public MenuResponseDto getMenuById(Long menuId) {
        Menu menu = menuMapper.findById(menuId);
        if (menu == null) {
            throw new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + menuId);
        }
        return toResponseDto(menu);
    }

    /**
     * 메뉴 등록
     */
    public MenuResponseDto createMenu(MenuRequestDto request) {
        Menu menu = new Menu();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuCode(request.getMenuCode());
        menu.setMenuUrl(request.getMenuUrl());
        menu.setDepth(request.getDepth() != null ? request.getDepth() : 1);
        menu.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        menu.setIcon(request.getIcon());
        menu.setUseYn(request.getUseYn() != null ? request.getUseYn() : true);
        menu.setDisplayYn(request.getDisplayYn() != null ? request.getDisplayYn() : true);

        // 상위 메뉴가 있으면 parent_name 설정
        if (request.getParentId() != null) {
            Menu parent = menuMapper.findById(request.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("상위 메뉴를 찾을 수 없습니다: " + request.getParentId());
            }
            menu.setParentName(parent.getMenuName());
            menu.setDepth(parent.getDepth() + 1);
        }

        menuMapper.insert(menu);

        return toResponseDto(menuMapper.findById(menu.getMenuId()));
    }

    /**
     * 메뉴 수정
     */
    public MenuResponseDto updateMenu(Long menuId, MenuRequestDto request) {
        Menu existing = menuMapper.findById(menuId);
        if (existing == null) {
            throw new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + menuId);
        }

        if (request.getParentId() != null) {
            existing.setParentId(request.getParentId());
            // 상위 메뉴명 갱신
            Menu parent = menuMapper.findById(request.getParentId());
            if (parent != null) {
                existing.setParentName(parent.getMenuName());
                existing.setDepth(parent.getDepth() + 1);
            }
        }
        if (request.getMenuName() != null) existing.setMenuName(request.getMenuName());
        if (request.getMenuCode() != null) existing.setMenuCode(request.getMenuCode());
        if (request.getMenuUrl() != null) existing.setMenuUrl(request.getMenuUrl());
        if (request.getDepth() != null) existing.setDepth(request.getDepth());
        if (request.getSortOrder() != null) existing.setSortOrder(request.getSortOrder());
        if (request.getIcon() != null) existing.setIcon(request.getIcon());
        if (request.getUseYn() != null) existing.setUseYn(request.getUseYn());
        if (request.getDisplayYn() != null) existing.setDisplayYn(request.getDisplayYn());

        menuMapper.update(existing);

        return toResponseDto(menuMapper.findById(menuId));
    }

    /**
     * 메뉴 삭제
     */
    public void deleteMenu(Long menuId) {
        Menu existing = menuMapper.findById(menuId);
        if (existing == null) {
            throw new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + menuId);
        }
        // 하위 메뉴의 parent_id를 NULL로 (스키마 FK ON DELETE SET NULL)
        menuMapper.deleteById(menuId);
    }

    // ── 트리 빌드 ──

    /**
     * 평면 목록을 계층형 트리로 조립한다.
     */
    private List<MenuResponseDto> buildTree(List<Menu> allMenus) {
        // 부모 ID별 그룹화
        Map<Long, List<Menu>> byParent = allMenus.stream()
                .collect(Collectors.groupingBy(m -> m.getParentId() == null ? 0L : m.getParentId()));

        // 루트 메뉴부터 트리 조립
        List<MenuResponseDto> tree = new ArrayList<>();
        List<Menu> roots = byParent.getOrDefault(0L, List.of());
        for (Menu root : roots) {
            tree.add(toTreeDto(root, byParent));
        }
        return tree;
    }

    private MenuResponseDto toTreeDto(Menu menu, Map<Long, List<Menu>> byParent) {
        MenuResponseDto dto = toResponseDto(menu);
        List<Menu> children = byParent.getOrDefault(menu.getMenuId(), List.of());
        dto.setChildren(children.stream()
                .map(c -> toTreeDto(c, byParent))
                .collect(Collectors.toList()));
        return dto;
    }

    // ── 변환 메서드 ──

    private MenuResponseDto toResponseDto(Menu menu) {
        MenuResponseDto dto = new MenuResponseDto();
        dto.setMenuId(menu.getMenuId());
        dto.setParentId(menu.getParentId());
        dto.setParentName(menu.getParentName());
        dto.setMenuName(menu.getMenuName());
        dto.setMenuCode(menu.getMenuCode());
        dto.setMenuUrl(menu.getMenuUrl());
        dto.setDepth(menu.getDepth());
        dto.setSortOrder(menu.getSortOrder());
        dto.setIcon(menu.getIcon());
        dto.setUseYn(menu.getUseYn());
        dto.setDisplayYn(menu.getDisplayYn());
        dto.setCreatedAt(menu.getCreatedAt());
        dto.setUpdatedAt(menu.getUpdatedAt());
        return dto;
    }
}