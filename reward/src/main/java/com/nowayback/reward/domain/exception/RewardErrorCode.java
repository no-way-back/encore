package com.nowayback.reward.domain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardErrorCode {

    // Validation
    VALIDATION_FAILED("RW-000", "입력 검증에 실패했습니다", HttpStatus.BAD_REQUEST),

    // System Error
    INTERNAL_SERVER_ERROR("RW-999", "일시적인 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}