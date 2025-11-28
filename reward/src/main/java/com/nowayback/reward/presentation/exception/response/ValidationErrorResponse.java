package com.nowayback.reward.presentation.exception.response;

import java.util.List;

public record ValidationErrorResponse(
        String code,
        String message,
        Integer status,
        List<FieldError> errors
) {}