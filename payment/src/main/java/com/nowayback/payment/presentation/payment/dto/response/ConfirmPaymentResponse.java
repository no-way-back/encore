package com.nowayback.payment.presentation.payment.dto.response;

import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConfirmPaymentResponse (
        UUID paymentId,
        UUID userId,
        UUID fundingId,
        Long amount,
        PaymentStatus status,
        String pgPaymentKey,
        String pgOrderId,
        String pgMethod,
        String refundAccountBank,
        String refundAccountNumber,
        String refundAccountHolderName,
        LocalDateTime approvedAt
) {

    public static ConfirmPaymentResponse from(PaymentResult result) {
        return new ConfirmPaymentResponse(
                result.paymentId(),
                result.userId(),
                result.fundingId(),
                result.amount(),
                result.status(),
                result.pgPaymentKey(),
                result.pgOrderId(),
                result.pgMethod(),
                result.refundAccountBank() != null ? result.refundAccountBank() : null,
                result.refundAccountNumber() != null ? result.refundAccountNumber() : null,
                result.refundAccountHolderName() != null ? result.refundAccountHolderName() : null,
                result.approvedAt()
        );
    }
}
