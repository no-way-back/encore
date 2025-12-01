package com.nowayback.funding.domain.event;

import java.util.UUID;

public record OutboxEventCreated(UUID eventId) {
}
