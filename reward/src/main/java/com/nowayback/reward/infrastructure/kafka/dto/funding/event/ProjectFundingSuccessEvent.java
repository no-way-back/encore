package com.nowayback.reward.infrastructure.kafka.dto.funding.event;

import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.payload.ProjectFundingSuccessPayload;

import java.util.UUID;

public record ProjectFundingSuccessEvent(
        UUID eventId,
        EventType eventType,
        ProjectFundingSuccessPayload payload
) {}