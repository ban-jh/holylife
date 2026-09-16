package org.shaloman.pj.service;

import org.shaloman.pj.domain.GroupCode;
import org.shaloman.pj.dto.GroupCodeRequestDto;
import org.shaloman.pj.dto.GroupCodeResponseDto;
import org.shaloman.pj.mapper.CommonCodeMapper;
import org.shaloman.pj.mapper.GroupCodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 그룹 코드 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class GroupCodeService {

    private final GroupCodeMapper groupCodeMapper;
    private final CommonCodeMapper commonCodeMapper;

    public GroupCodeService(GroupCodeMapper groupCodeMapper, CommonCodeMapper commonCodeMapper) {
        this.groupCodeMapper = groupCodeMapper;
        this.commonCodeMapper = commonCodeMapper;
    }

    /**
     * 전체 그룹 코드 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<GroupCodeResponseDto> getAllGroupCodes() {
        return groupCodeMapper.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 코드 검색 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<GroupCodeResponseDto> searchGroupCodes(String keyword) {
        return groupCodeMapper.findByKeyword(keyword).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 코드 상세 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public GroupCodeResponseDto getGroupCodeById(String groupCode) {
        GroupCode group = groupCodeMapper.findById(groupCode);
        if (group == null) {
            throw new IllegalArgumentException("그룹 코드를 찾을 수 없습니다: " + groupCode);
        }
        return toResponseDto(group);
    }

    /**
     * 그룹 코드 등록
     */
    public GroupCodeResponseDto createGroupCode(GroupCodeRequestDto request) {
        if (groupCodeMapper.findById(request.getGroupCode()) != null) {
            throw new IllegalArgumentException("이미 존재하는 그룹 코드입니다: " + request.getGroupCode());
        }

        GroupCode groupCode = new GroupCode();
        groupCode.setGroupCode(request.getGroupCode());
        groupCode.setGroupName(request.getGroupName());

        groupCodeMapper.insert(groupCode);

        return toResponseDto(groupCodeMapper.findById(groupCode.getGroupCode()));
    }

    /**
     * 그룹 코드 수정
     */
    public GroupCodeResponseDto updateGroupCode(String groupCode, GroupCodeRequestDto request) {
        GroupCode existing = groupCodeMapper.findById(groupCode);
        if (existing == null) {
            throw new IllegalArgumentException("그룹 코드를 찾을 수 없습니다: " + groupCode);
        }

        if (request.getGroupName() != null) existing.setGroupName(request.getGroupName());

        groupCodeMapper.update(existing);

        return toResponseDto(groupCodeMapper.findById(groupCode));
    }

    /**
     * 그룹 코드 삭제 (하위 공통 코드도 함께 삭제)
     */
    public void deleteGroupCode(String groupCode) {
        GroupCode existing = groupCodeMapper.findById(groupCode);
        if (existing == null) {
            throw new IllegalArgumentException("그룹 코드를 찾을 수 없습니다: " + groupCode);
        }
        // 하위 공통 코드 먼저 삭제
        commonCodeMapper.deleteByGroupCode(groupCode);
        groupCodeMapper.deleteById(groupCode);
    }

    // ── 변환 메서드 ──

    private GroupCodeResponseDto toResponseDto(GroupCode groupCode) {
        GroupCodeResponseDto dto = new GroupCodeResponseDto();
        dto.setGroupCode(groupCode.getGroupCode());
        dto.setGroupName(groupCode.getGroupName());
        dto.setModifiedDate(groupCode.getModifiedDate());
        return dto;
    }
}