package com.nowayback.funding.domain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum FundingErrorCode implements ErrorCode {

	// ================= Domain Errors =================
	INVALID_STATUS_TRANSITION("FD-DOM-001", "잘못된 상태 전환입니다.", HttpStatus.CONFLICT),
	INVALID_AMOUNT("FD-DOM-002", "유효하지 않은 금액입니다.", HttpStatus.BAD_REQUEST),
	INSUFFICIENT_CURRENT_AMOUNT("FD-DOM-004", "현재 모금액이 부족하여 차감할 수 없습니다.", HttpStatus.CONFLICT),
	NO_PARTICIPANTS_TO_DECREASE("FD-DOM-005", "참여자가 없어 차감할 수 없습니다.", HttpStatus.CONFLICT),
	CANNOT_CANCEL_NON_COMPLETED("FD-DOM-003", "완료된 펀딩만 취소할 수 있습니다.", HttpStatus.CONFLICT),

	// ================= Application Errors =================
	DUPLICATE_REQUEST("FD-APP-001", "이미 처리된 요청입니다.", HttpStatus.CONFLICT),
	FUNDING_PROCESS_FAILED("FD-APP-002", "후원 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	REWARD_DECREASE_FAILED("FD-APP-003", "리워드 재고 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	PAYMENT_PROCESS_FAILED("FD-APP-004", "결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	OUTBOX_EVENT_NOT_FOUND("FD-OUT-001", "해당 Outbox 이벤트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PROJECT_NOT_FOUND("FD-APP-005", "존재하지 않는 프로젝트입니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_FUNDING("FD-APP-006", "이미 해당 프로젝트에 후원하셨습니다.", HttpStatus.CONFLICT),
	PROJECT_NOT_ONGOING("FD-APP-007", "현재 후원할 수 없는 프로젝트입니다.", HttpStatus.CONFLICT),
	PROJECT_FUNDING_PERIOD_ENDED("FD-APP-008", "펀딩 기간이 종료되었습니다.", HttpStatus.CONFLICT),
	INVALID_FUNDING_AMOUNT("FD-APP-009", "요청된 펀딩 금액이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
	REWARD_NOT_FOUND("FD-APP-010", "존재하지 않는 리워드입니다.", HttpStatus.NOT_FOUND),
	FUNDING_NOT_FOUND("FD-APP-011", "존재하지 않는 후원 내역입니다.", HttpStatus.NOT_FOUND),
	UNAUTHORIZED_CANCEL("FD-APP-012", "본인의 후원 내역만 취소할 수 있습니다.", HttpStatus.FORBIDDEN),
	ALREADY_CANCELLED("FD-APP-013", "이미 취소된 후원입니다.", HttpStatus.CONFLICT),

	// ================= Infrastructure / External Errors =================
	REWARD_SERVICE_UNAVAILABLE("FD-INF-001", "티켓 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
	REWARD_BAD_REQUEST("FD-INF-002", "티켓 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	REWARD_CONFLICT("FD-INF-003", "티켓 재고가 부족합니다.", HttpStatus.CONFLICT),

	PAYMENT_SERVICE_UNAVAILABLE("FD-INF-101", "결제 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
	PAYMENT_BAD_REQUEST("FD-INF-102", "결제 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	PAYMENT_FAILED("FD-INF-103", "결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	REFUND_FAILED("FD-INF-104", "환불 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	EXTERNAL_SERVICE_TIMEOUT("FD-INF-900", "외부 서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT),
	EXTERNAL_SERVICE_ERROR("FD-INF-901", "외부 서비스에서 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY);


	private final String code;
	private final String message;
	private final HttpStatus status;

	FundingErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
