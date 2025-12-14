package com.nowayback.reward.domain.exception;

import lombok.Getter;

@Getter
public class RewardException extends RuntimeException {

    private final RewardErrorCode errorCode;

    public RewardException(RewardErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}