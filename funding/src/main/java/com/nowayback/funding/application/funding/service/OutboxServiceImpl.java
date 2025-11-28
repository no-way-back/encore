package com.nowayback.funding.application.funding.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

	private final OutboxRepository outboxRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markAsPublished(UUID eventId) {
		Outbox event = outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));
		event.markAsPublished();
		outboxRepository.save(event);
	}
}
