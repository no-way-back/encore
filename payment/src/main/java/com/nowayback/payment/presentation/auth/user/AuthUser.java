package com.nowayback.payment.presentation.auth.user;

import java.util.UUID;

public record AuthUser (
        UUID userId,
        String username,
        String role
){
}
