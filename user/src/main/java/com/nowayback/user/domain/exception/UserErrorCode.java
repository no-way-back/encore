package com.nowayback.user.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode {

    /**
     * Domain Errors
     */
    INVALID_USER_ROLE_FOR_CREATION("USER1001", "해당 역할의 사용자는 직접 생성할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ROLE_FOR_APPROVAL("USER1002", "해당 역할의 사용자는 승인할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS_FOR_APPROVAL("USER1003", "해당 상태의 사용자는 승인할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS_TRANSITION("USER1004", "사용자 상태를 해당 상태로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DEACTIVATED("USER1005", "사용자가 이미 비활성화 상태입니다.", HttpStatus.BAD_REQUEST),

    /**
     * Application Errors
     */
    DUPLICATE_USERNAME("USER2001", "이미 존재하는 아이디입니다.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("USER2002", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME("USER2003", "이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("USER2004", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    USER_STATUS_PENDING("USER2005", "가입 승인 대기 중인 사용자입니다.", HttpStatus.FORBIDDEN),
    USER_STATUS_SUSPENDED("USER2006", "정지된 사용자입니다.", HttpStatus.FORBIDDEN),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
