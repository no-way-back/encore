package com.nowayback.funding.application.client.reward.dto.response;

import java.util.UUID;

public record RewardDetailResponse(
	UUID rewardId,
	Long price,
	UUID optionId,
	Integer maxQuantity
) {}
