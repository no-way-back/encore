package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.domain.outbox.vo.EventPayload;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RewardCreatedEventPayload implements EventPayload {
    private UUID projectId;
}
