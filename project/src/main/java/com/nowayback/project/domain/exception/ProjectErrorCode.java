package com.nowayback.project.domain.exception;

import org.springframework.http.HttpStatus;

public enum ProjectErrorCode {

    NULL_USER_ID("PROJECT1001", "User ID는 필수입니다.", HttpStatus.BAD_REQUEST),
    NULL_TITLE("PROJECT1002", "프로젝트 제목은 필수입니다.", HttpStatus.BAD_REQUEST),
    NULL_SUMMARY("PROJECT1003", "프로젝트 요약은 필수입니다.", HttpStatus.BAD_REQUEST),
    NULL_CATEGORY("PROJECT1004", "카테고리는 필수입니다.", HttpStatus.BAD_REQUEST),
    NULL_CONTENT("PROJECT1005", "본문은 필수입니다.", HttpStatus.BAD_REQUEST),
    INVALID_GOAL_AMOUNT("PROJECT1006", "목표 금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    NULL_FUNDING_START("PROJECT1007", "펀딩 시작일은 필수입니다.", HttpStatus.BAD_REQUEST),
    NULL_FUNDING_END("PROJECT1008", "펀딩 종료일은 필수입니다.", HttpStatus.BAD_REQUEST),
    INVALID_FUNDING_PERIOD("PROJECT1009", "펀딩 종료일은 시작일 이후여야 합니다.", HttpStatus.BAD_REQUEST),

    INVALID_STATUS_FOR_START("PROJECT1010", "UPCOMING 상태만 LIVE로 변경 가능합니다.", HttpStatus.BAD_REQUEST),
    INVALID_STATUS_FOR_END("PROJECT1011", "LIVE 상태만 종료할 수 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_DRAFT_SUBMISSION("PROJECT1012", "모든 단계를 완료해야 제출할 수 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FUNDING_START_DATE("PROJECT1013", "펀딩 시작일은 오늘 이후여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_FUNDING_PERIOD_EQUAL("PROJECT1014", "펀딩 기간은 최소 1일 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_FUNDING_DRAFT_SUBMISSION("PROJECT1015", "펀딩 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_PRICE("PROJECT1016", "리워드 가격은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_LIMIT("PROJECT1017", "리워드 제한 수량은 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_SHIPPING_FEE("PROJECT1018", "배송비는 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_DRAFT_SUBMISSION("PROJECT1019", "리워드 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_OPTION_PRICE("PROJECT1020", "추가 금액은 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_OPTION_STOCK("PROJECT1021", "재고 수량은 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_OPTION_DISPLAY_ORDER("PROJECT1022", "표시 순서는 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_REWARD_OPTION_DRAFT_SUBMISSION("PROJECT1023", "리워드 옵션 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SETTLEMENT_BANK("PROJECT1024", "은행명은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SETTLEMENT_ACCOUNT_NUMBER("PROJECT1025", "계좌번호는 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SETTLEMENT_ACCOUNT_HOLDER("PROJECT1026", "예금주명은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SETTLEMENT_DRAFT_SUBMISSION("PROJECT1027", "정산 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STORY_TITLE("PROJECT1028", "제목은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STORY_SUMMARY("PROJECT1029", "요약은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STORY_CATEGORY("PROJECT1030", "카테고리는 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STORY_THUMBNAIL("PROJECT1031", "썸네일 URL은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STORY_CONTENT("PROJECT1032", "본문 JSON은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STORY_DRAFT_SUBMISSION("PROJECT1033", "스토리 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    ProjectErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }

    public HttpStatus getStatus() { return status; }
}
