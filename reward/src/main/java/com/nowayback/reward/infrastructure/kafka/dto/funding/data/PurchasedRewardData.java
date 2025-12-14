package com.nowayback.reward.infrastructure.kafka.dto.funding.data;

import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;

import java.util.UUID;

public record PurchasedRewardData(
        UUID rewardId,
        int purchasedQuantity
) {
    public CreateQRCodeCommand.PurchasedReward toCommand() {
        return new CreateQRCodeCommand.PurchasedReward(
                this.rewardId(),
                this.purchasedQuantity()
        );
    }
}