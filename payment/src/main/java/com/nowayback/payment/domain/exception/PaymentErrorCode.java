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
    NULL_PAYMENT_PROJECT_ID_VALUE("PAYMENT1003", "결제 프로젝트 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_MONEY_VALUE("PAYMENT1004", "유효하지 않은 금액 값입니다.", HttpStatus.BAD_REQUEST),

    /* Payment - PgInfo */
    NULL_PG_METHOD_VALUE("PAYMENT1005", "PG 결제 수단은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_PAYMENT_KEY_VALUE("PAYMENT1006", "PG 결제 키는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_ORDER_ID_VALUE("PAYMENT1007", "PG 주문 ID는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* Payment - RefundAccountInfo */
    NULL_REFUND_ACCOUNT_BANK_VALUE("PAYMENT1008", "환불 계좌 은행은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_REFUND_ACCOUNT_NUMBER_VALUE("PAYMENT1009", "환불 계좌 번호는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_REFUND_ACCOUNT_HOLDER_NAME_VALUE("PAYMENT1010", "환불 계좌 예금주 이름은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /* Payment Value Objects */
    NULL_USER_ID_OBJECT("PAYMENT1011", "유저 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_FUNDING_ID_OBJECT("PAYMENT1012", "펀딩 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PAYMENT_PROJECT_ID_OBJECT("PAYMENT1013", "결제 프로젝트 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PAYMENT_MONEY_OBJECT("PAYMENT1014", "금액 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PAYMENT_STATUS_OBJECT("PAYMENT1015", "결제 상태 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PG_INFO_OBJECT("PAYMENT1016", "PG 정보 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

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

    /**
     * Payment Status Log Domain Error Codes
     */
    NULL_PAYMENT_ID_OBJECT("PL1001", "결제 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PREV_PAYMENT_STATUS_OBJECT("PL1002", "이전 결제 상태 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_CURR_PAYMENT_STATUS_OBJECT("PL1003", "현재 결제 상태 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PAYMENT_AMOUNT_OBJECT("PL1004", "결제 금액 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /**
     * Settlement Status Log Domain Error Codes
     */
    NULL_SETTLEMENT_ID_OBJECT("SL1001", "정산 ID 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_PREV_SETTLEMENT_STATUS_OBJECT("SL1002", "이전 정산 상태 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_CURR_SETTLEMENT_STATUS_OBJECT("SL1003", "현재 정산 상태 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),
    NULL_SETTLEMENT_AMOUNT_OBJECT("SL1004", "정산 금액 객체는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST),

    /**
     * Payment Application Error Codes
     */
    PAYMENT_NOT_FOUND("PAYMENT2001", "결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /**
     * Settlement Application Error Codes
     */
    SETTLEMENT_NOT_FOUND("PAYMENT2002", "정산을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_SETTLEMENT("PAYMENT2003", "이미 정산이 처리된 프로젝트입니다.", HttpStatus.CONFLICT),

    /**
     * Payment Infrastructure Error Codes
     */
    PG_CONFIRMATION_FAILED("PAYMENT3001", "PG 결제 승인에 실패했습니다.", HttpStatus.BAD_GATEWAY),
    PG_REFUND_FAILED("PAYMENT3002", "PG 결제 환불에 실패했습니다.", HttpStatus.BAD_GATEWAY),

    /**
     * Settlement Infrastructure Error Codes
     */
    PROJECT_CLIENT_REQUEST_FAILED("SETTLEMENT3001", "프로젝트 외부 서비스 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY),
    OPEN_BANKING_TRANSFER_FAILED("SETTLEMENT3002", "오픈뱅킹 외부 서비스 이체 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY),

    /**
     * Presentation Error Codes
     */
    UNAUTHORIZED("PAYMENT4001", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("PAYMENT4002", "권한이 없는 사용자입니다.", HttpStatus.FORBIDDEN),
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
