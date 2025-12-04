package com.nowayback.funding.application.client.reward;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nowayback.funding.application.client.reward.dto.request.StockReserveRequest;
import com.nowayback.funding.application.client.reward.dto.response.StockReserveResponse;

import jakarta.validation.Valid;

@FeignClient(
	name = "reward-service",
	url = "${feign.client.config.reward-service.url}"
)
public interface RewardClient {

	/**
	 * 리워드 재고 예약 (차감)
	 *
	 * @param request 재고 예약 요청
	 * @return 예약 결과 (reservationId, totalAmount 포함)
	 */
	@PostMapping("/internal/rewards/reserve-stock")
	StockReserveResponse reserveStock(
		@Valid @RequestBody StockReserveRequest request
	);
}
