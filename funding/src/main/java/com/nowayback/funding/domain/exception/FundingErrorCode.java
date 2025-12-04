package com.nowayback.funding.domain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum FundingErrorCode implements ErrorCode {

	// ================= Domain Errors =================
	INVALID_STATUS_TRANSITION("FD-DOM-001", "잘못된 상태 전환입니다.", HttpStatus.CONFLICT),
	INVALID_AMOUNT("FD-DOM-002", "유효하지 않은 금액입니다.", HttpStatus.BAD_REQUEST),
	CANNOT_CANCEL_NON_COMPLETED("FD-DOM-003", "완료된 펀딩만 취소할 수 있습니다.", HttpStatus.CONFLICT),
	INSUFFICIENT_CURRENT_AMOUNT("FD-DOM-004", "현재 모금액이 부족하여 차감할 수 없습니다.", HttpStatus.CONFLICT),
	NO_PARTICIPANTS_TO_DECREASE("FD-DOM-005", "참여자가 없어 차감할 수 없습니다.", HttpStatus.CONFLICT),

	// ================= Application Errors =================
	DUPLICATE_REQUEST("FD-APP-001", "이미 처리된 요청입니다.", HttpStatus.CONFLICT),
	FUNDING_PROCESS_FAILED("FD-APP-002", "후원 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	PROJECT_NOT_FOUND("FD-APP-003", "존재하지 않는 프로젝트입니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_FUNDING("FD-APP-004", "이미 해당 프로젝트에 후원하셨습니다.", HttpStatus.CONFLICT),
	PROJECT_NOT_PROCESSING("FD-APP-005", "현재 후원할 수 없는 프로젝트입니다.", HttpStatus.CONFLICT),
	PROJECT_FUNDING_PERIOD_ENDED("FD-APP-006", "펀딩 기간이 종료되었습니다.", HttpStatus.CONFLICT),
	INVALID_FUNDING_AMOUNT("FD-APP-007", "요청된 펀딩 금액이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
	FUNDING_NOT_FOUND("FD-APP-008", "존재하지 않는 후원 내역입니다.", HttpStatus.NOT_FOUND),
	UNAUTHORIZED_CANCEL("FD-APP-009", "본인의 후원 내역만 취소할 수 있습니다.", HttpStatus.FORBIDDEN),
	ALREADY_CANCELLED("FD-APP-010", "이미 취소된 후원입니다.", HttpStatus.CONFLICT),
	FORBIDDEN_PROJECT_ACCESS("FD-APP-011", "프로젝트 생성자만 후원자 목록을 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
	PROJECT_ALREADY_EXISTS("FD-APP-012", "이미 존재하는 프로젝트입니다.", HttpStatus.CONFLICT),
	OUTBOX_EVENT_NOT_FOUND("FD-APP-013", "해당 Outbox 이벤트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	// ================= Infrastructure / External Errors - Reward Service =================
	REWARD_SERVICE_UNAVAILABLE("FD-INF-RW-001", "리워드 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
	REWARD_BAD_REQUEST("FD-INF-RW-002", "리워드 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	REWARD_NOT_FOUND("FD-INF-RW-003", "존재하지 않는 리워드입니다.", HttpStatus.NOT_FOUND),
	REWARD_CONFLICT("FD-INF-RW-004", "리워드 재고가 부족합니다.", HttpStatus.CONFLICT),

	// ================= Infrastructure / External Errors - Payment Service =================
	PAYMENT_SERVICE_UNAVAILABLE("FD-INF-PM-001", "결제 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
	PAYMENT_BAD_REQUEST("FD-INF-PM-002", "결제 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	PAYMENT_NOT_FOUND("FD-INF-PM-003", "존재하지 않는 결제 내역입니다.", HttpStatus.NOT_FOUND),
	PAYMENT_FAILED("FD-INF-PM-004", "결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	REFUND_FAILED("FD-INF-PM-005", "환불 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	// ================= Infrastructure / External Errors - Common =================
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
