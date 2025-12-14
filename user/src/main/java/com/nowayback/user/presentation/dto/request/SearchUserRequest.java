package com.nowayback.user.presentation.dto.request;

import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 검색 요청")
public record SearchUserRequest (
        @Schema(description = "검색 키워드", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
        String keyword,

        @Schema(description = "검색 필드 목록", example = "[\"username\", \"email\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> searchFields,

        @Schema(description = "역할", example = "USER", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        UserRole role,

        @Schema(description = "상태", example = "ACTIVE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        UserStatus status
) {
}
