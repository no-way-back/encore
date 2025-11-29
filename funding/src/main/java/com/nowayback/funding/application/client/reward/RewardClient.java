package com.nowayback.funding.application.client.reward;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nowayback.funding.application.client.reward.dto.request.DecreaseRewardRequest;
import com.nowayback.funding.application.client.reward.dto.request.RewardDetailsRequest;
import com.nowayback.funding.application.client.reward.dto.response.DecreaseRewardResponse;
import com.nowayback.funding.application.client.reward.dto.response.RewardDetailResponse;

@FeignClient(
	name = "reward-service",
	url = "${feign.client.config.reward-service.url}"
)
public interface RewardClient {

	@PostMapping("/internal/rewards/reserve-stock")
	DecreaseRewardResponse decreaseReward(@RequestBody DecreaseRewardRequest request);

	@PostMapping("/internal/rewards/details")
	List<RewardDetailResponse> getRewardDetails(@RequestBody RewardDetailsRequest request);
}
