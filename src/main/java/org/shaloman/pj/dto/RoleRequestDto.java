package org.shaloman.pj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 권한 그룹 등록/수정 요청 DTO
 */
@Data
@Schema(description = "권한 그룹 요청 DTO")
public class RoleRequestDto {

    @Schema(description = "그룹 코드 (예: ADMIN)", example = "ADMIN", required = true)
    @NotBlank(message = "그룹 코드는 필수입니다")
    @Size(max = 20, message = "그룹 코드는 20자 이하여야 합니다")
    private String groupCode;

    @Schema(description = "그룹명 (예: 관리자)", example = "관리자", required = true)
    @NotBlank(message = "그룹명은 필수입니다")
    @Size(max = 50, message = "그룹명은 50자 이하여야 합니다")
    private String groupName;

    @Schema(description = "그룹 설명", example = "전체 메뉴 접근 권한을 설정합니다.")
    @Size(max = 200, message = "그룹 설명은 200자 이하여야 합니다")
    private String groupDesc;

    @Schema(description = "사용 여부", example = "true")
    private Boolean useYn;
}