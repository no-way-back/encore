package com.nowayback.payment.presentation.settlement.dto.response;

import com.nowayback.payment.application.settlement.dto.result.SettlementStatusLogResult;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementStatusLogResponse (
        UUID settlementStatusLogId,
        SettlementStatus prevStatus,
        SettlementStatus currStatus,
        String reason,
        long amount,
        LocalDateTime createdAt
) {

    public static SettlementStatusLogResponse from(SettlementStatusLogResult result) {
        return new SettlementStatusLogResponse(
                result.settlementStatusLogId(),
                result.prevStatus(),
                result.currStatus(),
                result.reason(),
                result.amount(),
                result.createdAt()
        );
    }
}
