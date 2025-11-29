package com.nowayback.reward.application.reward.handler;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.domain.reward.handler.ProjectEventHandler;
import com.nowayback.reward.domain.reward.handler.command.RewardCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectEventHandlerImpl implements ProjectEventHandler {

    private final RewardService rewardService;

    @Transactional
    public void handle(UUID projectId, UUID creatorId, List<RewardCreateCommand> rewards) {
        rewardService.createRewardsForProject(projectId, creatorId, rewards);
    }

}