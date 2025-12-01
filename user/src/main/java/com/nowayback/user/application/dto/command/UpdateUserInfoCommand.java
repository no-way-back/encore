package com.nowayback.user.application.dto.command;

import java.util.UUID;

public record UpdateUserInfoCommand (
        UUID userId,
        String nickname
) {

    public static UpdateUserInfoCommand of(UUID userId, String nickname) {
        return new UpdateUserInfoCommand(userId, nickname);
    }
}
