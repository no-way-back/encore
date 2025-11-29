package com.nowayback.funding.application.client.reward.dto.request;

import java.util.List;
import java.util.UUID;

public record RewardDetailsRequest(
	List<UUID> rewardIds
) {}
