package com.nowayback.reward.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private Integer amount;

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
            throw new IllegalArgumentException("금액을 입력해주세요.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException(
                    String.format("금액은 0원 이상이어야 합니다. (입력값: %d원)", amount)
            );
        }
    }
}