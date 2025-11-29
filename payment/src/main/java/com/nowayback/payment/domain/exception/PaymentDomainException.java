package com.nowayback.payment.domain.exception;

import com.nowayback.payment.presentation.exception.PaymentErrorCode;
import com.nowayback.payment.presentation.exception.PaymentException;

public class PaymentDomainException extends PaymentException {

    public PaymentDomainException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
