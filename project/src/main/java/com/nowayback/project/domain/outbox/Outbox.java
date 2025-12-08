package com.nowayback.project.domain.outbox;

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
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventDestination destination;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payloadJson;

    @Column(name = "payload_type", nullable = false)
    private String payloadType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private Outbox(
        AggregateType aggregateType,
        UUID aggregateId,
        EventType eventType,
        EventDestination destination,
        String payloadJson,
        String payloadType
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.destination = destination;
        this.payloadJson = payloadJson;
        this.payloadType = payloadType;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public static Outbox create(
        AggregateType aggregateType,
        UUID aggregateId,
        EventType eventType,
        EventDestination destination,
        String payloadJson,
        String payloadType
    ) {
        return new Outbox(
            aggregateType,
            aggregateId != null ? aggregateId : UUID.randomUUID(),
            eventType,
            destination,
            payloadJson,
            payloadType
        );
    }

    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void incrementRetryCount() {
        this.retryCount += 1;
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }
}
