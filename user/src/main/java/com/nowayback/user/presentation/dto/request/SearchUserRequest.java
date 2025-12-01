package com.nowayback.user.presentation.dto.request;

import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;

import java.util.List;

public record SearchUserRequest (
        String keyword,
        List<String> searchFields,
        UserRole role,
        UserStatus status
) {
}
