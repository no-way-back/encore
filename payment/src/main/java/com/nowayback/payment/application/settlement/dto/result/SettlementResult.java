package com.nowayback.payment.application.settlement.dto.result;

import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementResult (
        UUID settlementId,
        UUID projectId,
        Long totalAmount,
        Long serviceFee,
        Long pgFee,
        Long netAmount,
        String accountBank,
        String accountNumber,
        String accountHolderName,
        SettlementStatus status,
        LocalDateTime requestedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {

    public static SettlementResult from(Settlement settlement) {
        return new SettlementResult(
                settlement.getId(),
                settlement.getProjectId().getId(),
                settlement.getTotalAmount().getAmount(),
                settlement.getServiceFee().getAmount(),
                settlement.getPgFee().getAmount(),
                settlement.getNetAmount().getAmount(),
                settlement.getAccountInfo().getAccountBank(),
                settlement.getAccountInfo().getAccountNumber(),
                settlement.getAccountInfo().getAccountHolderName(),
                settlement.getStatus(),
                settlement.getRequestedAt(),
                settlement.getCompletedAt(),
                settlement.getCreatedAt()
        );
    }
}
