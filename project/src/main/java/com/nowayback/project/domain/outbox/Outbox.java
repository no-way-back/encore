package com.nowayback.project.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventDestination;
import com.nowayback.project.domain.outbox.vo.EventType;
import com.nowayback.project.domain.outbox.vo.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Enumerated(EnumType.STRING)
	private AggregateType aggregateType;

	@Column(name = "aggregate_id", nullable = false)
	private UUID aggregateId;

	@Enumerated(EnumType.STRING)
	private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination", nullable = false)
    private EventDestination destination;

	@Column(name = "payload", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private OutboxStatus status;

	@Column(name = "retry_count", nullable = false)
	private Integer retryCount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	private Outbox(AggregateType aggregateType,
		UUID aggregateId,
		EventType eventType,
        EventDestination destination,
		String payload,
		OutboxStatus status,
		Integer retryCount,
		LocalDateTime createdAt) {
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
        this.destination = destination;
		this.payload = payload;
		this.status = status;
		this.retryCount = retryCount;
		this.createdAt = createdAt;
	}

	public static Outbox create(
		AggregateType aggregateType,
		UUID aggregateId,
		EventType eventType,
        EventDestination destination,
		Object payload) {
		return new Outbox(
			aggregateType,
			aggregateId != null ? aggregateId : UUID.randomUUID(),
			eventType,
            destination,
			toJson(payload),
			OutboxStatus.PENDING,
			0,
			LocalDateTime.now()
		);
	}

	public void markAsPublished() {
		this.status = OutboxStatus.PUBLISHED;
		this.publishedAt = LocalDateTime.now();
	}

	public void incrementRetryCount() {
		this.retryCount += 1;
	}

	private static String toJson(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Outbox 직렬화에 실패했습니다.", e);
		}
	}

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }
}
