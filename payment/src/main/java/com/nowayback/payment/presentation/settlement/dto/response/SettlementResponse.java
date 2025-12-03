package com.nowayback.payment.presentation.settlement.dto.response;

import com.nowayback.payment.application.settlement.dto.result.SettlementResult;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementResponse (
        UUID settlementId,
        UUID projectId,
        Long totalAmount,
        Long serviceFee,
        Long pgFee,
        Long netAmount,
        SettlementStatus status,
        LocalDateTime requestedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {

    public static SettlementResponse from(SettlementResult result) {
        return new SettlementResponse(
                result.settlementId(),
                result.projectId(),
                result.totalAmount(),
                result.serviceFee(),
                result.pgFee(),
                result.netAmount(),
                result.status(),
                result.requestedAt(),
                result.completedAt(),
                result.createdAt()
        );
    }
}
