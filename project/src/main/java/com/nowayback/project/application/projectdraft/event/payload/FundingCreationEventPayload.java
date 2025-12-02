package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.application.event.EventPayload;
import com.nowayback.project.application.projectdraft.dto.ProjectFundingDraftResult;
import java.time.LocalDateTime;
import java.util.UUID;

public class FundingCreationEventPayload implements EventPayload {
    private UUID projectId;
    private UUID creatorId;
    private Long targetAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private FundingCreationEventPayload(
        UUID projectId,
        UUID creatorId,
        Long targetAmount,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        this.projectId = projectId;
        this.creatorId = creatorId;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static FundingCreationEventPayload from(
        UUID projectId,
        UUID creatorId,
        ProjectFundingDraftResult fundingDraftResult
    ) {
        return new FundingCreationEventPayload(
            projectId,
            creatorId,
            fundingDraftResult.goalAmount(),
            fundingDraftResult.fundingStartDate().atStartOfDay(),
            fundingDraftResult.fundingEndDate().atStartOfDay()
        );
    }
}
