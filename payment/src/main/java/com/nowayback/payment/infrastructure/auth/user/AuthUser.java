package com.nowayback.payment.infrastructure.auth.user;

import java.util.UUID;

public record AuthUser (
        UUID userId,
        String username,
        String role
){
}
