package com.nowayback.user.presentation.exception.response;

public record ErrorResponse (
        String code,
        String message,
        Integer status
) {
}
