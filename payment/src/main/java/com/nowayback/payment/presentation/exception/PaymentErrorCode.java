package com.nowayback.payment.presentation.exception;

import org.springframework.http.HttpStatus;

public interface PaymentErrorCode {

    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
