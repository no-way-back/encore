package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.domain.outbox.vo.EventPayload;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RewardCreateResultEventPayload implements EventPayload {
    private String eventId;
    private String eventType;
    private LocalDateTime occurredAt;
    private Payload payload;

    @Getter
    public static class Payload {
        private UUID projectId;
        private boolean success;
    }
}
