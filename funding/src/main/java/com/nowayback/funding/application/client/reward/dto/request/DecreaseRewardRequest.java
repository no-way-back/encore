package com.nowayback.funding.application.client.reward.dto.request;

import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;

public record DecreaseRewardRequest(
	UUID fundingId,
	List<RewardItem> items
) {
	public static DecreaseRewardRequest from(UUID fundingId, CreateFundingCommand command) {
		List<RewardItem> rewardItems = command.rewardItems().stream()
			.map(item -> new RewardItem(
				item.rewardId(),
				item.optionId(),
				item.quantity()
			))
			.toList();

		return new DecreaseRewardRequest(fundingId, rewardItems);
	}

	public record RewardItem(
		Long rewardId,
		Long optionId,
		Integer quantity
	) {}
}
