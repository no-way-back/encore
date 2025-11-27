package com.nowayback.user.domain.exception;

import com.nowayback.user.presentation.exception.UserErrorCode;
import org.springframework.http.HttpStatus;

public enum UserDomainErrorCode implements UserErrorCode {

    INVALID_USER_ROLE_FOR_CREATION("USER1001", "해당 역할의 사용자는 직접 생성할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ROLE_FOR_APPROVAL("USER1002", "해당 역할의 사용자는 승인할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS_FOR_APPROVAL("USER1003", "해당 상태의 사용자는 승인할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS_TRANSITION("USER1004", "사용자 상태를 해당 상태로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DEACTIVATED("USER1005", "사용자가 이미 비활성화 상태입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    UserDomainErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
