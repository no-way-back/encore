package com.nowayback.payment.application.settlement.dto.result;

import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementStatusLogResult (
        UUID settlementStatusLogId,
        UUID settlementId,
        SettlementStatus prevStatus,
        SettlementStatus currStatus,
        String reason,
        long amount,
        LocalDateTime createdAt
) {

    public static SettlementStatusLogResult from(SettlementStatusLog settlementStatusLog) {
        return new SettlementStatusLogResult(
                settlementStatusLog.getId(),
                settlementStatusLog.getSettlementId(),
                settlementStatusLog.getPrevStatus(),
                settlementStatusLog.getCurrStatus(),
                settlementStatusLog.getReason(),
                settlementStatusLog.getAmount().getAmount(),
                settlementStatusLog.getCreatedAt()
        );
    }
}
