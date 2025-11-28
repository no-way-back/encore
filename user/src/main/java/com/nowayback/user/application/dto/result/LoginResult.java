package com.nowayback.user.application.dto.result;

import com.nowayback.user.domain.entity.User;

public record LoginResult (
        String accessToken,
        UserResult userResult
) {

    public static LoginResult from(String accessToken, User user) {
        UserResult userResult = UserResult.from(user);
        return new LoginResult(accessToken, userResult);
    }
}
