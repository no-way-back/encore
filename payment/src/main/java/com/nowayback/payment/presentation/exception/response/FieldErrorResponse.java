package com.nowayback.payment.presentation.exception.response;

import java.util.List;

public record FieldErrorResponse (
        String code,
        String message,
        int status,
        List<FieldError> errors
) {
}