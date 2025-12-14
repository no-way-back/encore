package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.domain.outbox.vo.EventPayload;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RewardCreateFailedEventPayload implements EventPayload {
    private UUID projectId;
}
