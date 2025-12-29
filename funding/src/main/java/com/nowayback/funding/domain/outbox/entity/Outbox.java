package com.nowayback.funding.domain.outbox.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nowayback.funding.domain.event.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "funding_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "aggregate_type", nullable = false, length = 50)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false)
	private UUID aggregateId;

	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false, length = 100)
	private EventType eventType;

	@Column(name = "payload", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(name = "payload_type", nullable = false, length = 500)
	private String payloadType;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private OutboxStatus status;

	@Column(name = "retry_count", nullable = false)
	private Integer retryCount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	private Outbox(String aggregateType,
				   UUID aggregateId,
				   EventType eventType,
				   String payload,
				   String payloadType,
				   OutboxStatus status,
				   Integer retryCount,
				   LocalDateTime createdAt) {
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
		this.payload = payload;
		this.payloadType = payloadType;
		this.status = status;
		this.retryCount = retryCount;
		this.createdAt = createdAt;
	}

	public static Outbox createOutbox(String aggregateType,
									  UUID aggregateId,
									  EventType eventType,
									  Object payload) {
		return new Outbox(
				aggregateType,
				aggregateId != null ? aggregateId : UUID.randomUUID(),
				eventType,
				toJson(payload),
				payload.getClass().getName(),
				OutboxStatus.PENDING,
				0,
				LocalDateTime.now()
		);
	}

	public void markAsPublished() {
		this.status = OutboxStatus.PUBLISHED;
		this.publishedAt = LocalDateTime.now();
	}

	public void markAsFailed() {
		this.status = OutboxStatus.FAILED;
	}

	public void incrementRetryCount() {
		this.retryCount += 1;
	}

	private static String toJson(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Outbox 직렬화에 실패했습니다.", e);
		}
	}
}