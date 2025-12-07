package com.nowayback.reward.infrastructure.kafka.dto.funding.payload;

import com.nowayback.reward.application.qrcode.command.CreateQRCodeCommand;
import com.nowayback.reward.infrastructure.kafka.dto.funding.data.PurchasedRewardData;

import java.util.List;
import java.util.UUID;

public record FundingCompletedPayload(
        UUID fundingId,
        String email,
        List<PurchasedRewardData> purchasedRewards
) {
    public CreateQRCodeCommand toCommand() {
        return new CreateQRCodeCommand(
                fundingId,
                email,
                purchasedRewards.stream()
                        .map(PurchasedRewardData::toCommand)
                        .toList()
        );
    }
}