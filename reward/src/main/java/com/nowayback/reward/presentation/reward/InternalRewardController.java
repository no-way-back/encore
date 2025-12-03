package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.presentation.reward.dto.response.RewardListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/rewards")
@RequiredArgsConstructor
public class InternalRewardController {

    private final RewardService rewardService;

    @GetMapping("/{projectId}")
    public ResponseEntity<RewardListResponse> getById(@PathVariable UUID projectId) {
        return ResponseEntity.ok(
                RewardListResponse.from(rewardService.getRewardsForProject(projectId))
        );
    }
}