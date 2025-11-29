package com.nowayback.reward.domain.reward.vo;

import com.nowayback.reward.domain.exception.RewardException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private Integer amount;
    private static final int MINIMUM_AMOUNT = 1000;

    private Money(Integer amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    public static Money of(Integer amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(0);
    }

    private void validateAmount(Integer amount) {
        if (amount == null) {
            throw new RewardException(INVALID_MONEY_AMOUNT);
        }
        if (amount < 0) {
            throw new RewardException(NEGATIVE_MONEY_AMOUNT);
        }
        if (amount < MINIMUM_AMOUNT) {
            throw new RewardException(PRICE_BELOW_MINIMUM);
        }
    }
}