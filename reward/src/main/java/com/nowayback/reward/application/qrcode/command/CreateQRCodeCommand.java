package com.nowayback.reward.application.qrcode.command;

import java.util.List;
import java.util.UUID;

public record CreateQRCodeCommand(
        UUID fundingId,
        String email,
        List<PurchasedReward> purchasedRewards
) {
    public record PurchasedReward(
            UUID rewardId,
            int purchasedQuantity
    ) {}
}