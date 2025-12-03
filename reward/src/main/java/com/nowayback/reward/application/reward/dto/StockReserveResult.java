package com.nowayback.reward.application.reward.dto;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;

import java.util.List;
import java.util.UUID;

public record StockReserveResult(
        UUID fundingId,
        List<ReservedItemResult> reservedItems,
        Integer totalAmount
) {
    public record ReservedItemResult(
            UUID reservationId,
            UUID rewardId,
            UUID optionId,
            Integer quantity,
            Integer unitPrice,
            Integer itemAmount
    ) {}

    public static StockReserveResult from(
            UUID fundingId,
            List<ReservationWithPrice> reservations
    ) {
        List<ReservedItemResult> items = reservations.stream()
                .map(r -> new ReservedItemResult(
                        r.reservation().getId(),
                        r.reservation().getRewardId().getId(),
                        r.reservation().getOptionId() != null ? r.reservation().getOptionId().getId() : null,
                        r.reservation().getQuantity(),
                        r.unitPrice(),
                        r.itemAmount()
                ))
                .toList();

        Integer totalAmount = items.stream()
                .mapToInt(ReservedItemResult::itemAmount)
                .sum();

        return new StockReserveResult(fundingId, items, totalAmount);
    }

    public record ReservationWithPrice(
            StockReservation reservation,
            Integer unitPrice,
            Integer itemAmount
    ) {
        public static ReservationWithPrice of(
                StockReservation reservation,
                Integer unitPrice,
                Integer itemAmount
        ) {
            return new ReservationWithPrice(reservation, unitPrice, itemAmount);
        }
    }
}