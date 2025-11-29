package com.nowayback.payment.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {
    /**
     * Payment Domain Error Codes
     */
    NULL_USER_ID_VALUE("PAYMENT1001", "유저 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_FUNDING_ID_VALUE("PAYMENT1002", "펀딩 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_MONEY_VALUE("PAYMENT1003", "유효하지 않은 금액 값입니다.", HttpStatus.BAD_REQUEST),

    /* Payment - PgInfo */
    NULL_PG_METHOD_VALUE("PAYMENT1004", "PG 결제 수단은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_PAYMENT_KEY_VALUE("PAYMENT1005", "PG 결제 키는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_TRANSACTION_ID_VALUE("PAYMENT1006", "PG 거래 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_ORDER_ID_VALUE("PAYMENT1007", "PG 주문 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* Payment - RefundAccountInfo */
    NULL_REFUND_ACCOUNT_BANK_VALUE("PAYMENT1008", "환불 계좌 은행은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_REFUND_ACCOUNT_NUMBER_VALUE("PAYMENT1009", "환불 계좌 번호는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_REFUND_ACCOUNT_HOLDER_NAME_VALUE("PAYMENT1010", "환불 계좌 예금주 이름은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* Payment Value Objects */
    NULL_USER_ID_OBJECT("PAYMENT1011", "유저 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_FUNDING_ID_OBJECT("PAYMENT1012", "펀딩 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PAYMENT_MONEY_OBJECT("PAYMENT1013", "금액 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PAYMENT_STATUS_OBJECT("PAYMENT1014", "결제 상태 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_INFO_OBJECT("PAYMENT1015", "PG 정보 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_REFUND_ACCOUNT_INFO_OBJECT("PAYMENT1016", "환불 정보 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    INVALID_PAYMENT_STATUS_TRANSITION("PAYMENT1017", "유효하지 않은 결제 상태 전환입니다.", HttpStatus.BAD_REQUEST),

    /**
     * Settlement Domain Error Codes
     */
    /* Settlement - Project Id */
    NULL_PROJECT_ID_VALUE("SETTLEMENT1001", "프로젝트 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* Settlement - Money */
    INVALID_SETTLEMENT_MONEY_VALUE("SETTLEMENT1002", "유효하지 않은 금액 값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_RATE("SETTLEMENT1003", "유효하지 않은 비율 값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PERCENTAGE("SETTLEMENT1004", "유효하지 않은 백분율 값입니다.", HttpStatus.BAD_REQUEST),

    /* Settlement - Account Info */
    NULL_ACCOUNT_BANK_VALUE("SETTLEMENT1005", "계좌 은행은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_ACCOUNT_NUMBER_VALUE("SETTLEMENT1006", "계좌 번호는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_ACCOUNT_HOLDER_NAME_VALUE("SETTLEMENT1007", "계좌 예금주 이름은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* Settlement Value Objects */
    NULL_PROJECT_ID_OBJECT("SETTLEMENT1008", "프로젝트 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_SETTLEMENT_TOTAL_AMOUNT_OBJECT("SETTLEMENT1009", "정산 총액 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_SETTLEMENT_ACCOUNT_INFO_OBJECT("SETTLEMENT1010", "정산 계좌 정보 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    INVALID_SETTLEMENT_STATUS_TRANSITION("SETTLEMENT1011", "유효하지 않은 정산 상태 전환입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    PaymentErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
