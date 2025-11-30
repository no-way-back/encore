package com.nowayback.user.presentation.dto.response;

import com.nowayback.user.application.dto.result.LoginResult;
import com.nowayback.user.domain.vo.UserRole;

import java.util.UUID;

public record LoginResponse (
        String accessToken,
        UUID id,
        String username,
        String nickname,
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