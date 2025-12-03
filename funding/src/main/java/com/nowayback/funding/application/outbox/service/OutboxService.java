package com.nowayback.funding.application.outbox.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nowayback.funding.domain.outbox.entity.Outbox;

public interface OutboxService {

	/**
	 * Outbox 이벤트를 발행 완료 상태로 변경
	 * @param eventId Outbox 이벤트 ID
	 */
	void markAsPublished(UUID eventId);

	/**
	 * Outbox 이벤트의 재시도 횟수 증가
	 * @param eventId Outbox 이벤트 ID
	 */
	void incrementRetryCount(UUID eventId);

	/**
	 * 성공 이벤트 발행 (메인 트랜잭션과 함께 커밋)
	 * 트랜잭션: REQUIRED
	 *
	 * @param aggregateType 집합 타입 (FUNDING, FUNDING_PROJECT 등)
	 * @param aggregateId 집합 ID (fundingId, projectId 등)
	 * @param eventType 이벤트 타입 (FUNDING_COMPLETED, PROJECT_FUNDING_SUCCESS 등)
	 * @param payload 이벤트 데이터
	 */
	void publishSuccessEvent(String aggregateType, UUID aggregateId, String eventType, Map<String, Object> payload);

	/**
	 * 보상 트랜잭션 이벤트 발행 (메인 트랜잭션 롤백과 독립적으로 커밋)
	 * 트랜잭션: REQUIRES_NEW
	 *
	 * @param aggregateType 집합 타입 (FUNDING 등)
	 * @param aggregateId 집합 ID (reservationId 등, nullable)
	 * @param eventType 이벤트 타입 (FUNDING_FAILED 등)
	 * @param payload 이벤트 데이터
	 */
	void publishCompensationEvent(String aggregateType, UUID aggregateId, String eventType, Map<String, Object> payload);

	/**
	 * 재시도 대상 Outbox 이벤트 조회
	 * @return PENDING 상태의 Outbox 이벤트 목록
	 */
	List<Outbox> getPendingEvents();
}
