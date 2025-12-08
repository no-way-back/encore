package com.nowayback.payment.presentation.payment.dto.response;

import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse (
        UUID paymentId,
        UUID userId,
        UUID fundingId,
        UUID projectId,
        long amount,
        PaymentStatus status,
        String pgMethod,
        String pgPaymentKey,
        String pgOrderId,
        String refundAccountBank,
        String refundAccountNumber,
        String refundAccountHolderName,
        String refundReason,
        LocalDateTime approvedAt,
        LocalDateTime refundedAt
) {

    public static PaymentResponse from(PaymentResult result) {
        return new PaymentResponse(
                result.paymentId(),
                result.userId(),
                result.fundingId(),
                result.projectId(),
                result.amount(),
                result.status(),
                result.pgMethod(),
                result.pgPaymentKey(),
                result.pgOrderId(),
                result.refundAccountBank(),
                result.refundAccountNumber(),
                result.refundAccountHolderName(),
                result.refundReason(),
                result.approvedAt(),
                result.refundedAt()
        );
    }
}
