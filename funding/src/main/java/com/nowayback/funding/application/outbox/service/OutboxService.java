package com.nowayback.funding.application.outbox.service;

import com.nowayback.funding.domain.event.OutboxEventMetadata;
import com.nowayback.funding.domain.outbox.entity.Outbox;

import java.util.List;
import java.util.UUID;

public interface OutboxService {

	Outbox findById(UUID eventId);

	/**
	 * Outbox 이벤트를 발행 완료 상태로 변경
	 * @param eventId Outbox 이벤트 ID
	 */
	void markAsPublished(UUID eventId);

	/**
	 * Outbox 이벤트를 발생 실패 상태로 변경
	 * @param eventId Outbox 이벤트 ID
	 */
	void markAsFailed(UUID eventId);

	/**
	 * Outbox 이벤트의 재시도 횟수 증가
	 * @param eventId Outbox 이벤트 ID
	 */
	void incrementRetryCount(UUID eventId);

	/**
	 * 성공 이벤트 발행 (메인 트랜잭션과 함께 커밋)
	 */
	void publish(OutboxEventMetadata event);

	/**
	 * 재시도 대상 Outbox 이벤트 조회
	 * @return PENDING 상태의 Outbox 이벤트 목록
	 */
	List<Outbox> getPendingEvents();
}
