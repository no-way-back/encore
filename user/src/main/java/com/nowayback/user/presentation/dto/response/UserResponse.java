package com.nowayback.user.presentation.dto.response;

import com.nowayback.user.application.dto.result.UserResult;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "유저 응답")
public record UserResponse (
        @Schema(description = "유저 ID", example = "000e0000-0000-0000-0000-000000000000")
        UUID userId,

        @Schema(description = "아이디", example = "user")
        String username,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "닉네임", example = "유저")
        String nickname,

        @Schema(description = "역할", example = "USER")
        UserRole role,

        @Schema(description = "상태", example = "ACTIVE")
        UserStatus status
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.id(),
                result.username(),
                result.email(),
                result.nickname(),
                result.role(),
                result.status()
        );
    }
}
