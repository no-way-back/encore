package com.nowayback.reward.domain.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "processed_project_events",
        indexes = @Index(name = "idx_event_id", columnList = "event_id", unique = true)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedProjectEvent {

    @Id
    @Column(name = "event_id", length = 100)
    private String eventId;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public ProcessedProjectEvent(String eventId, String projectId) {
        this.eventId = eventId;
        this.projectId = projectId;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 처리 완료된 이벤트 생성
     */
    public static ProcessedProjectEvent create(String eventId, String projectId) {
        return new ProcessedProjectEvent(eventId, projectId);
    }
}