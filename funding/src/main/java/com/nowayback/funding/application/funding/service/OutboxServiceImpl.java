package com.nowayback.funding.application.funding.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;
import com.nowayback.funding.domain.service.OutboxService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutboxServiceImpl implements OutboxService {

	private final OutboxRepository outboxRepository;

	public OutboxServiceImpl(OutboxRepository outboxRepository) {
		this.outboxRepository = outboxRepository;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markAsPublished(UUID eventId) {
		Outbox event = outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		event.markAsPublished();

		outboxRepository.save(event);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incrementRetryCount(UUID eventId) {
		Outbox event = outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		event.incrementRetryCount();

		outboxRepository.save(event);

		log.warn("Outbox 이벤트 재시도 횟수 증가 - eventId: {}, retryCount: {}",
			eventId, event.getRetryCount());
	}
}
