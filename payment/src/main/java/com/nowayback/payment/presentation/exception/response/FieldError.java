package com.nowayback.payment.presentation.exception.response;

public record FieldError (
        String field,
        String message
) {
}