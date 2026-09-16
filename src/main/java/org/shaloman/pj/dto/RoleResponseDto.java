package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 권한 그룹 응답 DTO
 */
@Data
@Schema(description = "권한 그룹 응답 DTO")
public class RoleResponseDto {

    @Schema(description = "권한 그룹 ID", example = "1")
    private Long roleId;

    @Schema(description = "그룹 코드", example = "ADMIN")
    private String groupCode;

    @Schema(description = "그룹명", example = "관리자")
    private String groupName;

    @Schema(description = "그룹 설명", example = "전체 메뉴 접근 권한을 설정합니다.")
    private String groupDesc;

    @Schema(description = "사용 여부", example = "true")
    private Boolean useYn;

    @Schema(description = "생성일시", example = "2026-09-01T10:00:00")
    private LocalDateTime createdAt;
}