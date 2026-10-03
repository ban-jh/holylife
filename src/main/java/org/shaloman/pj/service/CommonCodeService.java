package org.shaloman.pj.service;

import org.shaloman.pj.domain.CommonCode;
import org.shaloman.pj.domain.GroupCode;
import org.shaloman.pj.dto.CommonCodeRequestDto;
import org.shaloman.pj.dto.CommonCodeResponseDto;
import org.shaloman.pj.mapper.CommonCodeMapper;
import org.shaloman.pj.mapper.GroupCodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공통 코드 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;
    private final GroupCodeMapper groupCodeMapper;

    public CommonCodeService(CommonCodeMapper commonCodeMapper, GroupCodeMapper groupCodeMapper) {
        this.commonCodeMapper = commonCodeMapper;
        this.groupCodeMapper = groupCodeMapper;
    }

    /**
     * 전체 공통 코드 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<CommonCodeResponseDto> getAllCommonCodes() {
        return commonCodeMapper.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 코드에 속한 공통 코드 목록 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<CommonCodeResponseDto> getCommonCodesByGroupCode(String groupCode) {
        return commonCodeMapper.findByGroupCode(groupCode).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 코드 내 검색 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<CommonCodeResponseDto> searchCommonCodes(String groupCode, String keyword) {
        return commonCodeMapper.findByGroupCodeAndKeyword(groupCode, keyword).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 공통 코드 상세 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public CommonCodeResponseDto getCommonCodeById(Long codeId) {
        CommonCode code = commonCodeMapper.findById(codeId);
        if (code == null) {
            throw new IllegalArgumentException("공통 코드를 찾을 수 없습니다: " + codeId);
        }
        return toResponseDto(code);
    }

    /**
     * 공통 코드 등록.
     * 같은 그룹 내에 동일한 sort_order가 이미 존재하면,
     * 기존 코드들의 sort_order를 +1 하여 새 코드가 우선순위를 갖도록 한다.
     */
    public CommonCodeResponseDto createCommonCode(CommonCodeRequestDto request) {
        // 그룹 코드 존재 여부 확인
        GroupCode group = groupCodeMapper.findById(request.getGroupCode());
        if (group == null) {
            throw new IllegalArgumentException("존재하지 않는 그룹 코드입니다: " + request.getGroupCode());
        }

        int sortOrder = request.getSortOrder() != null ? request.getSortOrder() : 0;

        // 같은 그룹 내에서 sort_order >= 입력값인 기존 코드들을 +1 밀어내기
        commonCodeMapper.incrementSortOrderFrom(request.getGroupCode(), sortOrder, null);

        CommonCode commonCode = new CommonCode();
        commonCode.setGroupCode(request.getGroupCode());
        commonCode.setCode(request.getCode());
        commonCode.setCodeName(request.getCodeName());
        commonCode.setSortOrder(sortOrder);
        commonCode.setUseYn(request.getUseYn() != null ? request.getUseYn() : true);

        commonCodeMapper.insert(commonCode);

        return toResponseDto(commonCodeMapper.findById(commonCode.getCodeId()));
    }

    /**
     * 공통 코드 수정.
     * 같은 그룹 내에서 새 sort_order 이상인 기존 코드들(자기 자신 제외)의
     * sort_order를 +1 하여 순서 중복을 방지한다.
     * sort_order가 동일하게 유지되더라도 다른 코드와 중복될 수 있으므로
     * 항상 +1 밀어내기를 수행한다.
     */
    public CommonCodeResponseDto updateCommonCode(Long codeId, CommonCodeRequestDto request) {
        CommonCode existing = commonCodeMapper.findById(codeId);
        if (existing == null) {
            throw new IllegalArgumentException("공통 코드를 찾을 수 없습니다: " + codeId);
        }

        // sort_order가 변경되든 동일하든 항상 중복 처리
        if (request.getSortOrder() != null) {
            String groupCode = request.getGroupCode() != null ? request.getGroupCode() : existing.getGroupCode();
            commonCodeMapper.incrementSortOrderFrom(groupCode, request.getSortOrder(), codeId);
            existing.setSortOrder(request.getSortOrder());
        }

        if (request.getGroupCode() != null) existing.setGroupCode(request.getGroupCode());
        if (request.getCode() != null) existing.setCode(request.getCode());
        if (request.getCodeName() != null) existing.setCodeName(request.getCodeName());
        if (request.getUseYn() != null) existing.setUseYn(request.getUseYn());

        commonCodeMapper.update(existing);

        return toResponseDto(commonCodeMapper.findById(codeId));
    }

    /**
     * 공통 코드 삭제
     */
    public void deleteCommonCode(Long codeId) {
        CommonCode existing = commonCodeMapper.findById(codeId);
        if (existing == null) {
            throw new IllegalArgumentException("공통 코드를 찾을 수 없습니다: " + codeId);
        }
        commonCodeMapper.deleteById(codeId);
    }

    // ── 변환 메서드 ──

    private CommonCodeResponseDto toResponseDto(CommonCode commonCode) {
        CommonCodeResponseDto dto = new CommonCodeResponseDto();
        dto.setCodeId(commonCode.getCodeId());
        dto.setGroupCode(commonCode.getGroupCode());
        dto.setCode(commonCode.getCode());
        dto.setCodeName(commonCode.getCodeName());
        dto.setSortOrder(commonCode.getSortOrder());
        dto.setUseYn(commonCode.getUseYn());
        dto.setModifiedDate(commonCode.getModifiedDate());
        return dto;
    }
}