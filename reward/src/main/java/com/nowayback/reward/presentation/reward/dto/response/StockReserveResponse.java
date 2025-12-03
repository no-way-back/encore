package com.nowayback.reward.presentation.reward.dto.response;

import com.nowayback.reward.application.reward.dto.StockReserveResult;

import java.util.List;
import java.util.UUID;

public record StockReserveResponse(
        UUID fundingId,
        List<ReservedItem> reservedItems,
        Integer totalAmount
) {
    public record ReservedItem(
            UUID reservationId,
            UUID rewardId,
            UUID optionId,
            Integer quantity,
            Integer itemAmount
    ) {}

    public static StockReserveResponse from(StockReserveResult result) {
        List<ReservedItem> items = result.reservedItems().stream()
                .map(r -> new ReservedItem(
                        r.reservationId(),
                        r.rewardId(),
                        r.optionId(),
                        r.quantity(),
                        r.itemAmount()
                ))
                .toList();

        return new StockReserveResponse(result.fundingId(), items, result.totalAmount());
    }
}