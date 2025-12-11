package com.nowayback.reward.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RewardErrorCode {

    // Validation
    VALIDATION_FAILED("RW-000", "입력 검증에 실패했습니다", HttpStatus.BAD_REQUEST),

    // Reward Option
    DUPLICATE_OPTION_NAME("RW-001", "이미 존재하는 옵션명입니다", HttpStatus.CONFLICT),
    DUPLICATE_DISPLAY_ORDER("RW-002", "이미 사용 중인 표시 순서입니다", HttpStatus.CONFLICT),
    REQUIRED_OPTION_NOT_SELECTED("RW-003", "필수 옵션을 선택해야 합니다", HttpStatus.BAD_REQUEST),

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

    // Reward Validation
    REWARD_COUNT_EXCEEDED("RW-200", "리워드는 최대 10개까지 생성 가능합니다", HttpStatus.BAD_REQUEST),
    PRICE_BELOW_MINIMUM("RW-201", "금액은 최소 1,000원 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    STOCK_BELOW_MINIMUM("RW-202", "재고는 최소 1개 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    OPTION_COUNT_EXCEEDED("RW-203", "옵션은 최대 20개까지 추가 가능합니다", HttpStatus.BAD_REQUEST),
    OPTION_SOLD_OUT_ONLY("RW-204", "판매 이력이 있는 옵션은 품절 처리만 가능합니다", HttpStatus.BAD_REQUEST),
    OPTION_NOT_FOUND("RW-205", "존재하지 않는 옵션입니다", HttpStatus.NOT_FOUND),
    REWARD_NOT_FOUND("RW-206", "존재하지 않는 리워드입니다", HttpStatus.NOT_FOUND),
    INVALID_RESTORE_QUANTITY("RW-207", "복원 수량은 0보다 커야 합니다", HttpStatus.BAD_REQUEST),

    // QR Code
    QRCODE_NOT_FOUND("RW-400", "존재하지 않는 QR 코드입니다", HttpStatus.NOT_FOUND),
    QRCODE_ALREADY_USED("RW-401", "이미 사용된 QR 코드입니다", HttpStatus.CONFLICT),
    QRCODE_CANCELLED("RW-402", "취소된 QR 코드입니다", HttpStatus.BAD_REQUEST),

    // OpenFeign Project Service
    PROJECT_SERVICE_UNAVAILABLE("RW-501", "프로젝트 서비스 연결 실패", HttpStatus.SERVICE_UNAVAILABLE),
    PROJECT_BAD_REQUEST("RW-502", "잘못된 프로젝트 요청", HttpStatus.BAD_REQUEST),
    PROJECT_NOT_FOUND("RW-503", "프로젝트 정보 없음", HttpStatus.NOT_FOUND),
    PROJECT_CONFLICT("RW-504", "프로젝트 처리 충돌", HttpStatus.CONFLICT),
    PROJECT_SERVICE_TIMEOUT("RW-505", "프로젝트 서비스 타임아웃", HttpStatus.GATEWAY_TIMEOUT),

    // Required ID Validation
    CREATOR_ID_IS_NULL("RW-300", "생성자 ID는 null일 수 없습니다", HttpStatus.BAD_REQUEST),
    REWARD_ID_IS_NULL("RW-301", "리워드 ID는 null일 수 없습니다", HttpStatus.BAD_REQUEST),
    FUNDING_ID_IS_NULL("RW-302", "펀딩 ID는 null일 수 없습니다", HttpStatus.BAD_REQUEST),
    PROJECT_ID_IS_NULL("RW-303", "프로젝트 ID는 null일 수 없습니다", HttpStatus.BAD_REQUEST),

    // System Error
    INTERNAL_SERVER_ERROR("RW-999", "일시적인 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}