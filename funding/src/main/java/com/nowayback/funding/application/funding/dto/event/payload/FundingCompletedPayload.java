package com.nowayback.funding.application.funding.dto.event.payload;

import com.nowayback.funding.domain.funding.entity.Funding;

import java.util.List;
import java.util.UUID;

public record FundingCompletedPayload(
        UUID projectId,
        UUID fundingId,
        UUID userId,
        List<PurchasedRewardData> purchasedRewards
) {
    public static FundingCompletedPayload from(Funding funding) {
        return new FundingCompletedPayload(
                funding.getProjectId(),
                funding.getId(),
                funding.getUserId(),
                funding.getReservations().stream()
                        .map(reservation -> new PurchasedRewardData(
                                reservation.getRewardId(),
                                reservation.getQuantity()
                        ))
                        .toList()
        );
    }

    public record PurchasedRewardData(
            UUID rewardId,
            int purchasedQuantity
    ) {}
}