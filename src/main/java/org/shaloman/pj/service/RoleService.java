package org.shaloman.pj.service;

import org.shaloman.pj.domain.Role;
import org.shaloman.pj.dto.RoleRequestDto;
import org.shaloman.pj.dto.RoleResponseDto;
import org.shaloman.pj.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 권한 그룹 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class RoleService {

    private final RoleMapper roleMapper;

    public RoleService(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 전체 권한 그룹 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAllRoles() {
        return roleMapper.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 권한 그룹 검색 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDto> searchRoles(String keyword) {
        return roleMapper.findByKeyword(keyword).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용 여부로 권한 그룹 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getRolesByUseYn(Boolean useYn) {
        return roleMapper.findByUseYn(useYn).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 권한 그룹 상세 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(Long roleId) {
        Role role = roleMapper.findById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("권한 그룹을 찾을 수 없습니다: " + roleId);
        }
        return toResponseDto(role);
    }

    /**
     * 권한 그룹 등록
     */
    public RoleResponseDto createRole(RoleRequestDto request) {
        if (roleMapper.findByGroupCode(request.getGroupCode()) != null) {
            throw new IllegalArgumentException("이미 존재하는 그룹 코드입니다: " + request.getGroupCode());
        }

        Role role = new Role();
        role.setGroupCode(request.getGroupCode());
        role.setGroupName(request.getGroupName());
        role.setGroupDesc(request.getGroupDesc());
        role.setUseYn(request.getUseYn() != null ? request.getUseYn() : true);

        roleMapper.insert(role);

        return toResponseDto(roleMapper.findById(role.getRoleId()));
    }

    /**
     * 권한 그룹 수정
     */
    public RoleResponseDto updateRole(Long roleId, RoleRequestDto request) {
        Role existing = roleMapper.findById(roleId);
        if (existing == null) {
            throw new IllegalArgumentException("권한 그룹을 찾을 수 없습니다: " + roleId);
        }

        if (request.getGroupCode() != null) existing.setGroupCode(request.getGroupCode());
        if (request.getGroupName() != null) existing.setGroupName(request.getGroupName());
        if (request.getGroupDesc() != null) existing.setGroupDesc(request.getGroupDesc());
        if (request.getUseYn() != null) existing.setUseYn(request.getUseYn());

        roleMapper.update(existing);

        return toResponseDto(roleMapper.findById(roleId));
    }

    /**
     * 권한 그룹 삭제
     */
    public void deleteRole(Long roleId) {
        Role existing = roleMapper.findById(roleId);
        if (existing == null) {
            throw new IllegalArgumentException("권한 그룹을 찾을 수 없습니다: " + roleId);
        }
        // user_roles, permissions는 FK ON DELETE CASCADE로 자동 삭제됨
        roleMapper.deleteById(roleId);
    }

    // ── 변환 메서드 ──

    private RoleResponseDto toResponseDto(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setRoleId(role.getRoleId());
        dto.setGroupCode(role.getGroupCode());
        dto.setGroupName(role.getGroupName());
        dto.setGroupDesc(role.getGroupDesc());
        dto.setUseYn(role.getUseYn());
        dto.setCreatedAt(role.getCreatedAt());
        return dto;
    }
}