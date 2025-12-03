package com.nowayback.funding.application.client.reward.dto.request;

import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;

import jakarta.validation.constraints.NotNull;

public record DecreaseRewardRequest(
	List<RewardItem> items
) {
	public static DecreaseRewardRequest from(CreateFundingCommand command) {
		List<RewardItem> rewardItems = command.rewardItems().stream()
			.map(item -> new RewardItem(
				item.rewardId(),
				item.optionId(),
				item.quantity()
			))
			.toList();

		return new DecreaseRewardRequest(rewardItems);
	}

	public record RewardItem(
		@NotNull(message = "리워드 ID는 필수입니다.") UUID rewardId,
		@NotNull(message = "옵션 ID는 필수입니다.") UUID optionId,
		Integer quantity
	) {}
}
