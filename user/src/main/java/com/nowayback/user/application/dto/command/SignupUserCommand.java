package com.nowayback.user.application.dto.command;

import com.nowayback.user.domain.vo.UserRole;

public record SignupUserCommand (
        String username,
        String password,
        String email,
        String nickname,
        UserRole role
) {

    public static SignupUserCommand of(
            String username,
            String password,
            String email,
            String nickname,
            UserRole role
    ) {
        return new SignupUserCommand(username, password, email, nickname, role);
    }
}
