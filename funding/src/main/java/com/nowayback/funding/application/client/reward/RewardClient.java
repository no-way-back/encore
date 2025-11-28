package com.nowayback.funding.application.client.reward;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nowayback.funding.application.client.reward.dto.request.DecreaseRewardRequest;
import com.nowayback.funding.application.client.reward.dto.response.DecreaseRewardResponse;

@FeignClient(
	name = "reward-service",
	url = "${feign.client.config.reward-service.url"
)
public interface RewardClient {

	@PostMapping("/internal/rewards/reserve-stock")
	DecreaseRewardResponse decreaseReward(@RequestBody DecreaseRewardRequest request);
}
