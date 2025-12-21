package com.nowayback.reward.domain.outbox.entity;

import com.nowayback.reward.domain.outbox.vo.AggregateType;
import com.nowayback.reward.domain.outbox.vo.EventDestination;
import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.domain.outbox.vo.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_reward_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventDestination destination;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payloadJson;

    @Column(nullable = false, length = 500)
    private String payloadType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
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

    /**
     * Outbox 이벤트 생성
     *
     * @param aggregateType 이벤트 주체 타입
     * @param aggregateId 주체 ID (프로젝트 ID 등)
     * @param eventType 이벤트 타입
     * @param destination 발행 목적지 (Kafka Topic)
     * @param payloadJson 이미 JSON으로 직렬화된 문자열
     * @param payloadType 페이로드 클래스 타입 이름
     */
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

    /**
     * 발행 성공 처리
     */
    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    /**
     * 발행 실패 처리
     */
    public void markAsFailed(String errorMessage) {
        this.status = OutboxStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * 재시도 횟수 증가
     */
    public void incrementRetryCount() {
        this.retryCount += 1;
    }
}