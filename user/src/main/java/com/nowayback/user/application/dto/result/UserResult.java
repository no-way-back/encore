package com.nowayback.user.application.dto.result;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;

import java.util.UUID;

public record UserResult (
        UUID id,
        String username,
        String email,
        String nickname,
        UserRole role,
        UserStatus status
){
    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getStatus()
        );
    }
}
