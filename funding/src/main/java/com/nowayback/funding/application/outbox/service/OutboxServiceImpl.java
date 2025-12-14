package com.nowayback.funding.application.outbox.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.domain.event.OutboxEventCreated;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.outbox.repository.OutboxRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutboxServiceImpl implements OutboxService {

	private final OutboxRepository outboxRepository;
	private final ApplicationEventPublisher eventPublisher;

	public OutboxServiceImpl(OutboxRepository outboxRepository, ApplicationEventPublisher eventPublisher) {
		this.outboxRepository = outboxRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional(readOnly = true)
	public Outbox findById(UUID eventId) {
		return outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));
	}

	@Override
	@Transactional
	public void markAsPublished(UUID eventId) {
		Outbox event = outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		event.markAsPublished();

		outboxRepository.save(event);
	}

	@Override
	@Transactional
	public void markAsFailed(UUID eventId) {
		Outbox event = outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		event.markAsFailed();
		outboxRepository.save(event);

		log.error("Outbox 이벤트 FAILED 상태 변경 - eventId: {}, retryCount: {}, 수동 복구 필요",
			eventId, event.getRetryCount());
	}

	@Override
	@Transactional
	public void incrementRetryCount(UUID eventId) {
		Outbox event = outboxRepository.findById(eventId)
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		event.incrementRetryCount();
		outboxRepository.save(event);

		log.warn("Outbox 이벤트 재시도 횟수 증가 - eventId: {}, retryCount: {}",
			eventId, event.getRetryCount());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void publishSuccessEvent(
		String aggregateType,
		UUID aggregateId,
		String eventType,
		Map<String, Object> payload
	) {
		Outbox event = Outbox.createOutbox(
			aggregateType,
			aggregateId,
			eventType,
			payload
		);

		Outbox savedOutbox = outboxRepository.save(event);
		eventPublisher.publishEvent(new OutboxEventCreated(savedOutbox.getId()));

		log.info("성공 이벤트 발행 - aggregateType: {}, aggregateId: {}, eventType: {}",
			aggregateType, aggregateId, eventType);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void publishCompensationEvent(
		String aggregateType,
		UUID aggregateId,
		String eventType,
		Map<String, Object> payload
	) {
		Outbox event = Outbox.createOutbox(
			aggregateType,
			aggregateId,
			eventType,
			payload
		);

		Outbox savedOutbox = outboxRepository.save(event);
		eventPublisher.publishEvent(new OutboxEventCreated(savedOutbox.getId()));

		log.info("보상 트랜잭션 이벤트 발행 - aggregateType: {}, aggregateId: {}, eventType: {}",
			aggregateType, aggregateId, eventType);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Outbox> getPendingEvents() {
		return outboxRepository.findPendingEvents();
	}
}
