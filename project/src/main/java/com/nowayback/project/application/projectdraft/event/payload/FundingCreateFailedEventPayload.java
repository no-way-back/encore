package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.application.event.EventPayload;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class FundingCreateFailedEventPayload implements EventPayload {
    private UUID projectId;
    private UUID creatorId;
    private Long targetAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String failureReason;
}
