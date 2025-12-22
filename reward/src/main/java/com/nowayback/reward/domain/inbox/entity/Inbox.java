package com.nowayback.reward.domain.inbox.entity;

import com.nowayback.reward.domain.inbox.vo.InboxStatus;
import com.nowayback.reward.domain.outbox.vo.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_inbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inbox {

    @Id
    @Column(name = "event_id", columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private EventType eventType;

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "UUID")
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InboxStatus status;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    private Inbox(
            UUID id,
            EventType eventType,
            UUID aggregateId,
            String payload
    ) {
        this.id = id;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = InboxStatus.RECEIVED;
        this.receivedAt = LocalDateTime.now();
        this.retryCount = 0;
    }

    public static Inbox create(
            UUID eventId,
            EventType eventType,
            UUID aggregateId,
            String payload
    ) {
        return new Inbox(eventId, eventType, aggregateId, payload);
    }

    /**
     * 이미 처리된 이벤트인지 확인
     */
    public boolean isProcessed() {
        return this.status == InboxStatus.PROCESSED;
    }

    /**
     * 처리 완료 상태로 변경
     */
    public void markAsProcessed() {
        this.status = InboxStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 처리 실패 상태로 변경 (DLQ 전송 후 호출)
     */
    public void markAsFailed(String errorMessage) {
        this.status = InboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }

    /**
     * 재시도 횟수 증가
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
}