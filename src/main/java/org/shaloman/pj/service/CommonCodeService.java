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
     * 공통 코드 등록
     */
    public CommonCodeResponseDto createCommonCode(CommonCodeRequestDto request) {
        // 그룹 코드 존재 여부 확인
        GroupCode group = groupCodeMapper.findById(request.getGroupCode());
        if (group == null) {
            throw new IllegalArgumentException("존재하지 않는 그룹 코드입니다: " + request.getGroupCode());
        }

        CommonCode commonCode = new CommonCode();
        commonCode.setGroupCode(request.getGroupCode());
        commonCode.setCode(request.getCode());
        commonCode.setCodeName(request.getCodeName());
        commonCode.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        commonCode.setUseYn(request.getUseYn() != null ? request.getUseYn() : true);

        commonCodeMapper.insert(commonCode);

        return toResponseDto(commonCodeMapper.findById(commonCode.getCodeId()));
    }

    /**
     * 공통 코드 수정
     */
    public CommonCodeResponseDto updateCommonCode(Long codeId, CommonCodeRequestDto request) {
        CommonCode existing = commonCodeMapper.findById(codeId);
        if (existing == null) {
            throw new IllegalArgumentException("공통 코드를 찾을 수 없습니다: " + codeId);
        }

        if (request.getGroupCode() != null) existing.setGroupCode(request.getGroupCode());
        if (request.getCode() != null) existing.setCode(request.getCode());
        if (request.getCodeName() != null) existing.setCodeName(request.getCodeName());
        if (request.getSortOrder() != null) existing.setSortOrder(request.getSortOrder());
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