package com.nowayback.project.application.project.event.payload;

import com.nowayback.project.domain.outbox.vo.EventPayload;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ProjectFundingSuccessPayload implements EventPayload {
    private UUID projectId;
    private Long finalAmount;
    private Integer participantCount;
}
