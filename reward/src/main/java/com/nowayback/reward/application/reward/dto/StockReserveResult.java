package com.nowayback.reward.application.reward.dto;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;

import java.util.List;
import java.util.UUID;

public record StockReserveResult(
        UUID fundingId,
        List<ReservedItemResult> reservedItems,
        Long totalAmount
) {
    public record ReservedItemResult(
            UUID reservationId,
            UUID rewardId,
            UUID optionId,
            Integer quantity,
            Long itemAmount
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
                        r.itemAmount()
                ))
                .toList();

        Long totalAmount = items.stream()
                .mapToLong(ReservedItemResult::itemAmount)
                .sum();

        return new StockReserveResult(fundingId, items, totalAmount);
    }

    public record ReservationWithPrice(
            StockReservation reservation,
            Long itemAmount
    ) {
        public static ReservationWithPrice of(
                StockReservation reservation,
                Long itemAmount
        ) {
            return new ReservationWithPrice(reservation, itemAmount);
        }
    }
}