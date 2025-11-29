package com.nowayback.reward.domain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardErrorCode {

    // Validation
    VALIDATION_FAILED("RW-000", "입력 검증에 실패했습니다", HttpStatus.BAD_REQUEST),

    // Reward Option
    DUPLICATE_OPTION_NAME("RW-001", "이미 존재하는 옵션명입니다", HttpStatus.BAD_REQUEST),
    DUPLICATE_DISPLAY_ORDER("RW-002", "이미 사용 중인 표시 순서입니다", HttpStatus.BAD_REQUEST),

    // Value Object - Money
    INVALID_MONEY_AMOUNT("RW-100", "금액을 입력해주세요", HttpStatus.BAD_REQUEST),
    NEGATIVE_MONEY_AMOUNT("RW-101", "금액은 0원 이상이어야 합니다", HttpStatus.BAD_REQUEST),

    // Value Object - Stock
    INVALID_STOCK_QUANTITY("RW-110", "재고 수량을 입력해주세요", HttpStatus.BAD_REQUEST),
    NEGATIVE_STOCK_QUANTITY("RW-111", "재고 수량은 0 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("RW-112", "재고가 부족합니다", HttpStatus.BAD_REQUEST),

    // Value Object - ShippingPolicy
    INVALID_SHIPPING_FEE("RW-120", "배송비를 입력해주세요", HttpStatus.BAD_REQUEST),
    NEGATIVE_SHIPPING_FEE("RW-121", "배송비는 0원 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    NEGATIVE_FREE_SHIPPING_AMOUNT("RW-122", "무료배송 기준금액은 0원 이상이어야 합니다", HttpStatus.BAD_REQUEST),

    // System Error
    INTERNAL_SERVER_ERROR("RW-999", "일시적인 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}