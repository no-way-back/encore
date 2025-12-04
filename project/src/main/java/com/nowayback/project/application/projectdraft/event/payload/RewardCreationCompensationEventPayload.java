package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.application.event.EventPayload;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RewardCreationCompensationEventPayload implements EventPayload {
    private UUID projectId;

    private RewardCreationCompensationEventPayload(UUID projectId) {
        this.projectId = projectId;
    }

    public static RewardCreationCompensationEventPayload from(UUID projectId) {
        return new RewardCreationCompensationEventPayload(projectId);
    }
}
