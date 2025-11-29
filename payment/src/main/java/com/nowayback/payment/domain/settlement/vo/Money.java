package com.nowayback.payment.domain.settlement.vo;


import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
            throw new PaymentDomainException(PaymentDomainErrorCode.INVALID_SETTLEMENT_MONEY_VALUE);
        }
        return new Money(amount);
    }

    public Money add(Money other) {
        return Money.of(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        return Money.of(this.amount - other.amount);
    }

    public Money multiplyByRate(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException(PaymentDomainErrorCode.INVALID_RATE);
        }

        BigDecimal result = BigDecimal.valueOf(this.amount)
                .multiply(rate)
                .setScale(0, RoundingMode.FLOOR);

        return Money.of(result.longValue());
    }

    public Money multiplyByPercent(int percent) {
        if (percent < 0) {
            throw new PaymentDomainException(PaymentDomainErrorCode.INVALID_PERCENTAGE);
        }
        return multiplyByRate(BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100)));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount > other.amount;
    }

    public boolean isZero() {
        return this.amount == 0;
    }
}
