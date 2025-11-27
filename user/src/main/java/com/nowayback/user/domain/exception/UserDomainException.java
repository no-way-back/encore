package com.nowayback.user.domain.exception;

import com.nowayback.user.presentation.exception.UserException;

public class UserDomainException extends UserException {

    public UserDomainException(UserDomainErrorCode errorCode) {
        super(errorCode);
    }
}
