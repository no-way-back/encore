package com.nowayback.payment.presentation.exception.response;

public record ErrorResponse (
        String code,
        String message,
        Integer status
) {
}