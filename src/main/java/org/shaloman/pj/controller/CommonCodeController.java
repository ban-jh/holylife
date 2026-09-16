package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.shaloman.pj.dto.CommonCodeRequestDto;
import org.shaloman.pj.dto.CommonCodeResponseDto;
import org.shaloman.pj.service.CommonCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공통 코드 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/common-codes")
@Tag(name = "공통코드 관리", description = "그룹 코드 / 공통 코드 CRUD API")
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    public CommonCodeController(CommonCodeService commonCodeService) {
        this.commonCodeService = commonCodeService;
    }

    @GetMapping
    @Operation(summary = "공통 코드 목록 조회", description = "전체 또는 그룹 코드에 속한 공통 코드 목록을 조회한다. keyword 파라미터 시 검색한다.")
    public ResponseEntity<List<CommonCodeResponseDto>> getCommonCodes(
            @Parameter(description = "그룹 코드") @RequestParam(required = false) String groupCode,
            @Parameter(description = "검색어 (코드/코드명)") @RequestParam(required = false) String keyword) {
        if (groupCode != null && !groupCode.isBlank()) {
            if (keyword != null && !keyword.isBlank()) {
                return ResponseEntity.ok(commonCodeService.searchCommonCodes(groupCode, keyword));
            }
            return ResponseEntity.ok(commonCodeService.getCommonCodesByGroupCode(groupCode));
        }
        return ResponseEntity.ok(commonCodeService.getAllCommonCodes());
    }

    @GetMapping("/{codeId}")
    @Operation(summary = "공통 코드 상세 조회", description = "코드 ID로 단일 공통 코드 정보를 조회한다.")
    public ResponseEntity<CommonCodeResponseDto> getCommonCodeById(
            @Parameter(description = "코드 ID", example = "101")
            @PathVariable Long codeId) {
        return ResponseEntity.ok(commonCodeService.getCommonCodeById(codeId));
    }

    @PostMapping
    @Operation(summary = "공통 코드 등록", description = "새로운 공통 코드를 등록한다.")
    public ResponseEntity<CommonCodeResponseDto> createCommonCode(@Valid @RequestBody CommonCodeRequestDto request) {
        CommonCodeResponseDto created = commonCodeService.createCommonCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{codeId}")
    @Operation(summary = "공통 코드 수정", description = "기존 공통 코드 정보를 수정한다.")
    public ResponseEntity<CommonCodeResponseDto> updateCommonCode(
            @Parameter(description = "코드 ID", example = "101")
            @PathVariable Long codeId,
            @Valid @RequestBody CommonCodeRequestDto request) {
        return ResponseEntity.ok(commonCodeService.updateCommonCode(codeId, request));
    }

    @DeleteMapping("/{codeId}")
    @Operation(summary = "공통 코드 삭제", description = "공통 코드 ID로 공통 코드를 삭제한다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommonCode(
            @Parameter(description = "코드 ID", example = "101")
            @PathVariable Long codeId) {
        commonCodeService.deleteCommonCode(codeId);
    }
}