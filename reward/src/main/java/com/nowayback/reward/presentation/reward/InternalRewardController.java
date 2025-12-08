package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.presentation.reward.dto.request.StockReserveRequest;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import com.nowayback.reward.presentation.reward.dto.response.StockReserveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/rewards")
@RequiredArgsConstructor
public class InternalRewardController {

    private final RewardService rewardService;
    private final RewardStockService rewardStockService;

    @GetMapping("/{projectId}")
    public ResponseEntity<RewardListResponse> getById(@PathVariable UUID projectId) {
        return ResponseEntity.ok(
                RewardListResponse.from(rewardService.getRewardsForProject(projectId))
        );
    }

    @PostMapping("/reserve-stock")
    public ResponseEntity<StockReserveResponse> reserveStock(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @Valid @RequestBody StockReserveRequest request
    ) {
        StockReserveCommand command = StockReserveCommand.from(userId, request);
        StockReserveResult result = rewardStockService.reserveStock(command);

        return ResponseEntity.ok(StockReserveResponse.from(result));
    }
}