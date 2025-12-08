package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.presentation.reward.dto.request.UpdateRewardRequest;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Reward", description = "리워드 관리 API")
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @Operation(summary = "프로젝트의 리워드 목록 조회", description = "특정 프로젝트에 속한 모든 리워드를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getById(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable("projectId") UUID projectId
    ) {
        return ResponseEntity.ok(
                RewardListResponse
                        .from(rewardService.getRewardsForProject(projectId))
        );
    }

    @Operation(summary = "리워드 정보 수정", description = "리워드의 정보를 수정합니다.")
    @PatchMapping("/{rewardId}")
    public ResponseEntity<UpdateRewardRequest> updateReward(
            @Parameter(description = "리워드 ID", required = true)
            @PathVariable UUID rewardId,
            @RequestBody UpdateRewardRequest request
    ) {
        UpdateRewardCommand command = UpdateRewardCommand.from(rewardId, request);
        rewardService.update(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "리워드 삭제", description = "리워드를 삭제합니다.")
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<Void> deleteReward(
            @Parameter(description = "리워드 ID", required = true)
            @PathVariable UUID rewardId
    ) {
        rewardService.delete(rewardId);

        return ResponseEntity.noContent().build();
    }
}