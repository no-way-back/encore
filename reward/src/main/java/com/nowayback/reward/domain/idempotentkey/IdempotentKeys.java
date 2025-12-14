package com.nowayback.reward.domain.idempotentkey;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 처리 완료된 프로젝트 이벤트 기록
 * Kafka에서 수신한 PROJECT_CREATED 이벤트의 중복 처리 방지
 */
@Entity
@Table(name = "p_idempotent_keys")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotentKeys {

    @Id
    @Column(name = "event_id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public IdempotentKeys(UUID id, String projectId) {
        this.id = id;
        this.projectId = projectId;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 처리 완료된 이벤트 생성
     */
    public static IdempotentKeys create(UUID eventId, String projectId) {
        return new IdempotentKeys(eventId, projectId);
    }
}