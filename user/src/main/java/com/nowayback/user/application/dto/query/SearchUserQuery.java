package com.nowayback.user.application.dto.query;

import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public record SearchUserQuery (
        String keyword,
        List<String> searchFields,
        UserRole role,
        UserStatus status,
        Pageable pageable
) {

    public static SearchUserQuery of(
            String keyword,
            List<String> searchFields,
            UserRole role,
            UserStatus status,
            Pageable pageable
    ) {
        return new SearchUserQuery(
                keyword,
                searchFields,
                role,
                status,
                pageable
        );
    }
}
