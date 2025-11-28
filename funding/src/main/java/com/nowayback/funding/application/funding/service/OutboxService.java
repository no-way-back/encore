package com.nowayback.funding.application.funding.service;

import java.util.UUID;

public interface OutboxService {

	void markAsPublished(UUID eventId);
}
