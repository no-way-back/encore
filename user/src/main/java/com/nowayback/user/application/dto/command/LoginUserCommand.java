package com.nowayback.user.application.dto.command;

public record LoginUserCommand (
        String username,
        String password
) {

    public static LoginUserCommand of(String username, String password) {
        return new LoginUserCommand(username, password);
    }
}
