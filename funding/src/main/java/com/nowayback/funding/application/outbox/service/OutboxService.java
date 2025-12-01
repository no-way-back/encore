package com.nowayback.funding.application.outbox.service;

import java.util.UUID;

public interface OutboxService {

	void markAsPublished(UUID eventId);

	void incrementRetryCount(UUID id);
}
