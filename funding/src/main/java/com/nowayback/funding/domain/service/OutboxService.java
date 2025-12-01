package com.nowayback.funding.domain.service;

import java.util.UUID;

public interface OutboxService {

	void markAsPublished(UUID eventId);

	void incrementRetryCount(UUID id);
}
