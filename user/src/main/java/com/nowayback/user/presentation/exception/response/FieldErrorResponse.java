package com.nowayback.user.presentation.exception.response;

import java.util.List;

public record FieldErrorResponse (
        String code,
        String message,
        int status,
        List<FieldError> errors
) {
}
