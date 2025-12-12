package com.nowayback.reward.domain.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 처리 완료된 프로젝트 이벤트 기록
 * Kafka에서 수신한 PROJECT_CREATED 이벤트의 중복 처리 방지
 */
@Entity
@Table(
        name = "p_idempotent_keys",
        indexes = @Index(name = "idx_event_id", columnList = "event_id", unique = true)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotentKeys {

    @Id
    @Column(name = "event_id", length = 100)
    private String eventId;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public IdempotentKeys(String eventId, String projectId) {
        this.eventId = eventId;
        this.projectId = projectId;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 처리 완료된 이벤트 생성
     */
    public static IdempotentKeys create(String eventId, String projectId) {
        return new IdempotentKeys(eventId, projectId);
    }
}