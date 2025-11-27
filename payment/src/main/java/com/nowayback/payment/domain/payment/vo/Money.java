package com.nowayback.payment.domain.payment.vo;

import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private long amount;

    private Money(long amount) {
        this.amount = amount;
    }

    public static Money of(long amount) {
        if (amount < 0) {
            throw new PaymentDomainException(PaymentDomainErrorCode.INVALID_MONEY_VALUE);
        }
        return new Money(amount);
    }

    public Money add(Money other) {
        return Money.of(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        return Money.of(this.amount - other.amount);
    }

    public boolean isGreaterThan(Money other) {
        return this.amount > other.amount;
    }

    public boolean isZero() {
        return this.amount == 0;
    }
}
