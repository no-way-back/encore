package com.nowayback.payment.presentation.payment.dto.response;

import com.nowayback.payment.application.payment.dto.result.PaymentStatusLogResult;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentStatusLogResponse (
        UUID paymentStatusLogId,
        PaymentStatus prevStatus,
        PaymentStatus currStatus,
        String reason,
        long amount,
        LocalDateTime createdAt
) {

    public static PaymentStatusLogResponse from(PaymentStatusLogResult result) {
        return new PaymentStatusLogResponse(
                result.paymentStatusLogId(),
                result.prevStatus(),
                result.currStatus(),
                result.reason(),
                result.amount(),
                result.createdAt()
        );
    }
}
