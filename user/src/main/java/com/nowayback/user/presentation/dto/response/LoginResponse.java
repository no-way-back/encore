package com.nowayback.user.presentation.dto.response;

import com.nowayback.user.application.dto.result.LoginResult;
import com.nowayback.user.domain.vo.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "로그인 응답")
public record LoginResponse (
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "사용자 ID", example = "00000000-0000-0000-0000-000000000000")
        UUID id,

        @Schema(description = "아이디", example = "user")
        String username,

        @Schema(description = "닉네임", example = "유저")
        String nickname,

        @Schema(description = "역할", example = "USER")
        UserRole role
) {

    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.userResult().id(),
                result.userResult().username(),
                result.userResult().nickname(),
                result.userResult().role()
        );
    }
}