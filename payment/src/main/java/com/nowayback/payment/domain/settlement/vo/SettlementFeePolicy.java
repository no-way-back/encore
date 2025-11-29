package com.nowayback.payment.domain.settlement.vo;

import java.math.BigDecimal;

public class SettlementFeePolicy {

    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.05");
    private static final BigDecimal PG_FEE_RATE = new BigDecimal("0.03");

    public Money calculateServiceFee(Money totalAmount) {
        return totalAmount.multiplyByRate(SERVICE_FEE_RATE);
    }

    public Money calculatePgFee(Money totalAmount) {
        return totalAmount.multiplyByRate(PG_FEE_RATE);
    }

    public Money calculateNetAmount(Money totalAmount, Money serviceFee, Money pgFee) {
        return totalAmount.subtract(serviceFee).subtract(pgFee);
    }
}
