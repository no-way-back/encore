package com.nowayback.reward.domain.reward.vo;

import com.nowayback.reward.domain.exception.RewardException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.nowayback.reward.domain.exception.RewardErrorCode.INVALID_MONEY_AMOUNT;
import static com.nowayback.reward.domain.exception.RewardErrorCode.NEGATIVE_MONEY_AMOUNT;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private Long amount;

    private Money(Long amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    public static Money of(Long amount) {
        return new Money(amount);
    }

    private void validateAmount(Long amount) {
        if (amount == null) {
            throw new RewardException(INVALID_MONEY_AMOUNT);
        }
        if (amount < 0) {
            throw new RewardException(NEGATIVE_MONEY_AMOUNT);
        }
    }
}