package com.nowayback.reward.application.reward.command;

import com.nowayback.reward.presentation.reward.dto.request.StockReserveRequest;

import java.util.List;
import java.util.UUID;

public record StockReserveCommand(
        UUID userId,
        UUID fundingId,
        List<StockReserveItemCommand> items
) {
    public record StockReserveItemCommand(
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {}

    public static StockReserveCommand from(UUID userId, StockReserveRequest request) {
        List<StockReserveItemCommand> items = request.items().stream()
                .map(item -> new StockReserveItemCommand(
                        item.rewardId(),
                        item.optionId(),
                        item.quantity()
                ))
                .toList();

        return new StockReserveCommand(userId, request.fundingId(), items);
    }
}