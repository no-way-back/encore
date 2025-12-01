package com.nowayback.funding.domain.funding.event;

import java.util.UUID;

public record OutboxEventCreated(UUID eventId) {
}
