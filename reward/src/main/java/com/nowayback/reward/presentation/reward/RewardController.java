package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.presentation.reward.dto.request.UpdateRewardRequest;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController implements RewardControllerDoc {

    private final RewardService rewardService;

    @Override
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getById(@PathVariable("projectId") UUID projectId) {
        return ResponseEntity.ok(
                RewardListResponse
                        .from(rewardService.getRewardsForProject(projectId))
        );
    }

    @Override
    @PatchMapping("/{rewardId}")
    public ResponseEntity<UpdateRewardRequest> updateReward(
            @PathVariable UUID rewardId,
            @RequestBody UpdateRewardRequest request
    ) {
        UpdateRewardCommand command = UpdateRewardCommand.from(rewardId, request);
        rewardService.update(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<Void> deleteReward(@PathVariable UUID rewardId) {
        rewardService.delete(rewardId);

        return ResponseEntity.noContent().build();
    }
}