package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.application.event.EventPayload;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RewardCreatedEventPayload implements EventPayload {
    private UUID projectId;
}
