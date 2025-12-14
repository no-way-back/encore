package com.nowayback.funding.application.funding.dto.command;

import java.util.List;
import java.util.UUID;

public record CreateFundingCommand(
	UUID projectId,
	UUID userId,
	List<RewardItem> rewardItems,
	Long amount,
	String pgPaymentKey,
	String pgOrderId,
	String pgMethod,
	String idempotencyKey
) {
	public record RewardItem(
		UUID rewardId,
		UUID optionId,
		Integer quantity
	) {
	}
		public boolean hasRewards() {
			return rewardItems != null && !rewardItems.isEmpty();
	}
}
