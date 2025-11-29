package com.nowayback.funding.application.funding.event.dto;

import java.util.UUID;

public record OutboxEventCreated(UUID eventId) {
}
