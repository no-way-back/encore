package com.nowayback.user.presentation.exception.response;

public record FieldError (
        String field,
        String message
) {
}
