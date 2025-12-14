package com.nowayback.reward.presentation.exception.response;

public record FieldError(
        String field,
        String message
) {}