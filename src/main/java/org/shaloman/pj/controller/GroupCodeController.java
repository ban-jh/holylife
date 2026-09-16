package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.shaloman.pj.dto.GroupCodeRequestDto;
import org.shaloman.pj.dto.GroupCodeResponseDto;
import org.shaloman.pj.service.GroupCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 그룹 코드 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/group-codes")
@Tag(name = "공통코드 관리", description = "그룹 코드 / 공통 코드 CRUD API")
public class GroupCodeController {

    private final GroupCodeService groupCodeService;

    public GroupCodeController(GroupCodeService groupCodeService) {
        this.groupCodeService = groupCodeService;
    }

    @GetMapping
    @Operation(summary = "전체 그룹 코드 조회", description = "등록된 모든 그룹 코드 목록을 조회한다. keyword 파라미터 시 검색한다.")
    public ResponseEntity<List<GroupCodeResponseDto>> getAllGroupCodes(
            @Parameter(description = "검색어 (그룹코드명/그룹코드)") @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(groupCodeService.searchGroupCodes(keyword));
        }
        return ResponseEntity.ok(groupCodeService.getAllGroupCodes());
    }

    @GetMapping("/{groupCode}")
    @Operation(summary = "그룹 코드 상세 조회", description = "그룹 코드로 단일 그룹 코드 정보를 조회한다.")
    public ResponseEntity<GroupCodeResponseDto> getGroupCodeById(
            @Parameter(description = "그룹 코드", example = "USER_STATUS")
            @PathVariable String groupCode) {
        return ResponseEntity.ok(groupCodeService.getGroupCodeById(groupCode));
    }

    @PostMapping
    @Operation(summary = "그룹 코드 등록", description = "새로운 그룹 코드를 등록한다.")
    public ResponseEntity<GroupCodeResponseDto> createGroupCode(@Valid @RequestBody GroupCodeRequestDto request) {
        GroupCodeResponseDto created = groupCodeService.createGroupCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{groupCode}")
    @Operation(summary = "그룹 코드 수정", description = "기존 그룹 코드 정보를 수정한다.")
    public ResponseEntity<GroupCodeResponseDto> updateGroupCode(
            @Parameter(description = "그룹 코드", example = "USER_STATUS")
            @PathVariable String groupCode,
            @Valid @RequestBody GroupCodeRequestDto request) {
        return ResponseEntity.ok(groupCodeService.updateGroupCode(groupCode, request));
    }

    @DeleteMapping("/{groupCode}")
    @Operation(summary = "그룹 코드 삭제", description = "그룹 코드를 삭제한다. 하위 공통 코드도 함께 삭제된다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupCode(
            @Parameter(description = "그룹 코드", example = "USER_STATUS")
            @PathVariable String groupCode) {
        groupCodeService.deleteGroupCode(groupCode);
    }
}