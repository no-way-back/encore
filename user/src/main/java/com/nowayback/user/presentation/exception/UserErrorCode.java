package com.nowayback.user.presentation.exception;

import org.springframework.http.HttpStatus;

public interface UserErrorCode {

    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
