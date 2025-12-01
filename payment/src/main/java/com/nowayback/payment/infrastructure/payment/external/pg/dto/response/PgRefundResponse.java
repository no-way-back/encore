package com.nowayback.payment.infrastructure.payment.external.pg.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record PgRefundResponse(
        String paymentKey,
        String orderId,
        List<CancelInfo> cancels,
        String status
) {
    public record CancelInfo(
            String cancelReason,
            OffsetDateTime canceledAt,
            String cancelStatus,
            Long cancelAmount
    ) {}
}
