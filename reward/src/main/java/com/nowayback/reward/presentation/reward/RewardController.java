package com.nowayback.reward.presentation.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.presentation.reward.dto.request.UpdateRewardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @PatchMapping("/{rewardId}")
    public ResponseEntity<UpdateRewardRequest> updateReward(
            @PathVariable UUID rewardId,
            @RequestBody UpdateRewardRequest request
    ) {
        UpdateRewardCommand command = UpdateRewardCommand.from(rewardId, request);
        rewardService.update(command);

        return ResponseEntity.noContent().build();
    }
}
