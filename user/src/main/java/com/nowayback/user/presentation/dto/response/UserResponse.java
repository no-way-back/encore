package com.nowayback.user.presentation.dto.response;

import com.nowayback.user.application.dto.result.UserResult;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;

import java.util.UUID;

public record UserResponse (
        UUID userId,
        String username,
        String email,
        String nickname,
        UserRole role,
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
