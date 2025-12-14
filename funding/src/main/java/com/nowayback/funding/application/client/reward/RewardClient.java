package com.nowayback.funding.application.client.reward;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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
		@RequestHeader("X-User-Id") UUID userId,
		@Valid @RequestBody StockReserveRequest request
	);
}
