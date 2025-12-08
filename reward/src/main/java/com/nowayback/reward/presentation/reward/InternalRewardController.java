package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.presentation.reward.dto.request.StockReserveRequest;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import com.nowayback.reward.presentation.reward.dto.response.StockReserveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Internal Reward", description = "내부 마이크로서비스 간 통신용 리워드 API")
@RestController
@RequestMapping("/internal/rewards")
@RequiredArgsConstructor
public class InternalRewardController {

    private final RewardService rewardService;
    private final RewardStockService rewardStockService;

    @Operation(summary = "프로젝트의 리워드 목록 조회", description = "특정 프로젝트에 속한 모든 리워드를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<RewardListResponse> getById(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                RewardListResponse.from(rewardService.getRewardsForProject(projectId))
        );
    }

    @Operation(summary = "리워드 재고 예약", description = "펀딩 시 리워드 재고를 예약합니다.")
    @PostMapping("/reserve-stock")
    public ResponseEntity<StockReserveResponse> reserveStock(
            @Parameter(description = "사용자 ID", required = true)
            @RequestHeader(value = "X-User-Id") UUID userId,
            @Valid @RequestBody StockReserveRequest request
    ) {
        StockReserveCommand command = StockReserveCommand.from(userId, request);
        StockReserveResult result = rewardStockService.reserveStock(command);

        return ResponseEntity.ok(StockReserveResponse.from(result));
    }
}