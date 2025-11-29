package com.nowayback.reward.domain.reward.handler;


import com.nowayback.reward.domain.reward.handler.command.RewardCreateCommand;

import java.util.List;
import java.util.UUID;

public interface ProjectEventHandler {
    void handle(UUID projectId, UUID creatorId, List<RewardCreateCommand> rewards);
}
