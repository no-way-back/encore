package com.nowayback.funding.infrastructure.messaging.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectCreatedEvent(
	UUID projectId,
	Long targetAmount,
	LocalDateTime startDate,
	LocalDateTime endDate
) {}
