package com.nowayback.user.infrastructure.auth.user;

import java.util.UUID;

public record AuthUser (
        UUID userId,
        String username,
        String role
){
}
