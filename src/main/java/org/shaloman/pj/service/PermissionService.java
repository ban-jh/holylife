package org.shaloman.pj.service;

import org.shaloman.pj.domain.Menu;
import org.shaloman.pj.domain.Permission;
import org.shaloman.pj.dto.MenuPermissionResponseDto;
import org.shaloman.pj.dto.PermissionRequestDto;
import org.shaloman.pj.dto.PermissionResponseDto;
import org.shaloman.pj.mapper.MenuMapper;
import org.shaloman.pj.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 권한 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class PermissionService {

    private final PermissionMapper permissionMapper;
    private final MenuMapper menuMapper;

    public PermissionService(PermissionMapper permissionMapper, MenuMapper menuMapper) {
        this.permissionMapper = permissionMapper;
        this.menuMapper = menuMapper;
    }

    /**
     * 전체 권한 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> getAllPermissions() {
        return permissionMapper.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 권한 그룹 ID로 권한 목록 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.findByRoleId(roleId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 권한 상세 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public PermissionResponseDto getPermissionById(Long permissionId) {
        Permission permission = permissionMapper.findById(permissionId);
        if (permission == null) {
            throw new IllegalArgumentException("권한을 찾을 수 없습니다: " + permissionId);
        }
        return toResponseDto(permission);
    }

    /**
     * 특정 role_id에 대한 메뉴 권한 트리 조회 (읽기 전용)
     * 전체 메뉴 트리 + 각 메뉴의 체크(권한) 상태를 반환한다.
     */
    @Transactional(readOnly = true)
    public List<MenuPermissionResponseDto> getMenuPermissionTreeByRoleId(Long roleId) {
        // 전체 메뉴 평면 목록
        List<Menu> allMenus = menuMapper.findAll();
        // role_id에 해당하는 권한 목록
        List<Permission> permissions = permissionMapper.findByRoleId(roleId);
        // menu_id → permission 매핑
        Map<Long, Permission> permByMenuId = permissions.stream()
                .collect(Collectors.toMap(Permission::getMenuId, p -> p, (a, b) -> a));

        // 트리 조립
        Map<Long, List<Menu>> byParent = allMenus.stream()
                .collect(Collectors.groupingBy(m -> m.getParentId() == null ? 0L : m.getParentId()));

        List<MenuPermissionResponseDto> tree = new ArrayList<>();
        List<Menu> roots = byParent.getOrDefault(0L, List.of());
        for (Menu root : roots) {
            tree.add(toMenuPermissionTreeDto(root, byParent, permByMenuId));
        }
        return tree;
    }

    /**
     * 권한 등록
     */
    public PermissionResponseDto createPermission(PermissionRequestDto request) {
        // 중복 체크 (uk_permissions: role_id + menu_id)
        if (permissionMapper.findByRoleAndMenu(request.getRoleId(), request.getMenuId()) != null) {
            throw new IllegalArgumentException("이미 등록된 권한입니다 (roleId=" + request.getRoleId() + ", menuId=" + request.getMenuId() + ")");
        }

        Permission permission = new Permission();
        permission.setRoleId(request.getRoleId());
        permission.setMenuId(request.getMenuId());
        permission.setCanRead(request.getCanRead() != null ? request.getCanRead() : false);
        permission.setCanWrite(request.getCanWrite() != null ? request.getCanWrite() : false);
        permission.setCanDelete(request.getCanDelete() != null ? request.getCanDelete() : false);

        permissionMapper.insert(permission);

        return toResponseDto(permissionMapper.findById(permission.getPermissionId()));
    }

    /**
     * 권한 수정
     */
    public PermissionResponseDto updatePermission(Long permissionId, PermissionRequestDto request) {
        Permission existing = permissionMapper.findById(permissionId);
        if (existing == null) {
            throw new IllegalArgumentException("권한을 찾을 수 없습니다: " + permissionId);
        }

        if (request.getRoleId() != null) existing.setRoleId(request.getRoleId());
        if (request.getMenuId() != null) existing.setMenuId(request.getMenuId());
        if (request.getCanRead() != null) existing.setCanRead(request.getCanRead());
        if (request.getCanWrite() != null) existing.setCanWrite(request.getCanWrite());
        if (request.getCanDelete() != null) existing.setCanDelete(request.getCanDelete());

        permissionMapper.update(existing);

        return toResponseDto(permissionMapper.findById(permissionId));
    }

    /**
     * 특정 role_id의 권한 일괄 업데이트 (bulk update)
     * 기존 권한을 모두 삭제하고, 요청받은 권한 목록으로 재등록한다.
     *
     * @param roleId    권한 그룹 ID
     * @param requests  권한 목록 (체크된 메뉴 목록)
     */
    public void bulkUpdatePermissionsByRoleId(Long roleId, List<PermissionRequestDto> requests) {
        // 기존 권한 전체 삭제
        permissionMapper.deleteByRoleId(roleId);

        // 새 권한 일괄 등록
        if (requests != null) {
            for (PermissionRequestDto req : requests) {
                Permission permission = new Permission();
                permission.setRoleId(roleId);
                permission.setMenuId(req.getMenuId());
                permission.setCanRead(req.getCanRead() != null ? req.getCanRead() : true);
                permission.setCanWrite(req.getCanWrite() != null ? req.getCanWrite() : false);
                permission.setCanDelete(req.getCanDelete() != null ? req.getCanDelete() : false);
                permissionMapper.insert(permission);
            }
        }
    }

    /**
     * 권한 삭제
     */
    public void deletePermission(Long permissionId) {
        Permission existing = permissionMapper.findById(permissionId);
        if (existing == null) {
            throw new IllegalArgumentException("권한을 찾을 수 없습니다: " + permissionId);
        }
        permissionMapper.deleteById(permissionId);
    }

    // ── 트리 빌드 ──

    private MenuPermissionResponseDto toMenuPermissionTreeDto(Menu menu,
                                                               Map<Long, List<Menu>> byParent,
                                                               Map<Long, Permission> permByMenuId) {
        MenuPermissionResponseDto dto = new MenuPermissionResponseDto();
        dto.setMenuId(menu.getMenuId());
        dto.setParentId(menu.getParentId());
        dto.setMenuName(menu.getMenuName());
        dto.setMenuCode(menu.getMenuCode());
        dto.setMenuUrl(menu.getMenuUrl());
        dto.setDepth(menu.getDepth());

        Permission perm = permByMenuId.get(menu.getMenuId());
        if (perm != null) {
            dto.setCanRead(perm.getCanRead());
            dto.setCanWrite(perm.getCanWrite());
            dto.setCanDelete(perm.getCanDelete());
            dto.setChecked(perm.getCanRead() != null && perm.getCanRead());
        } else {
            dto.setCanRead(false);
            dto.setCanWrite(false);
            dto.setCanDelete(false);
            dto.setChecked(false);
        }

        List<Menu> children = byParent.getOrDefault(menu.getMenuId(), List.of());
        dto.setChildren(children.stream()
                .map(c -> toMenuPermissionTreeDto(c, byParent, permByMenuId))
                .collect(Collectors.toList()));

        return dto;
    }

    // ── 변환 메서드 ──

    private PermissionResponseDto toResponseDto(Permission permission) {
        PermissionResponseDto dto = new PermissionResponseDto();
        dto.setPermissionId(permission.getPermissionId());
        dto.setRoleId(permission.getRoleId());
        dto.setMenuId(permission.getMenuId());
        dto.setCanRead(permission.getCanRead());
        dto.setCanWrite(permission.getCanWrite());
        dto.setCanDelete(permission.getCanDelete());
        dto.setCreatedAt(permission.getCreatedAt());
        return dto;
    }
}