package com.nowayback.reward.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}