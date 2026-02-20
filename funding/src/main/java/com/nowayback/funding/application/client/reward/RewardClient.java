package com.nowayback.funding.application.client.reward;

import com.nowayback.funding.application.client.reward.dto.request.StockReserveRequest;
import com.nowayback.funding.application.client.reward.dto.response.StockReserveResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "reward-service",
        url = "${feign.client.config.reward-service.url}"
)
public interface RewardClient {

    /**
     * 리워드 재고 예약 (차감)
     */
    @PostMapping("/internal/rewards/reserve-stock")
    StockReserveResponse reserveStock(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody StockReserveRequest request
    );

    /**
     * 재고 예약 확정 (펀딩 생성 성공 후)
     */
    @PostMapping("/internal/rewards/confirm-reservations/{fundingId}")
    void confirmReservations(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID fundingId
    );

    /**
     * 재고 복원 (펀딩 실패/취소 시)
     */
    @PostMapping("/internal/rewards/restore-stock/{fundingId}")
    void restoreStock(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID fundingId
    );
}