package com.nowayback.reward.presentation.exception.response;

public record ErrorResponse(
        String code,
        String message,
        Integer status
) {}