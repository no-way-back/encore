package com.nowayback.payment.application.payment.dto.result;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentStatusLogResult (
        UUID paymentStatusLogId,
        PaymentStatus prevStatus,
        PaymentStatus currStatus,
        String reason,
        long amount,
        LocalDateTime createdAt
) {

    public static PaymentStatusLogResult from(PaymentStatusLog paymentStatusLog) {
        return new PaymentStatusLogResult(
                paymentStatusLog.getId(),
                paymentStatusLog.getPrevStatus(),
                paymentStatusLog.getCurrStatus(),
                paymentStatusLog.getReason(),
                paymentStatusLog.getAmount().getAmount(),
                paymentStatusLog.getCreatedAt()
        );
    }
}
