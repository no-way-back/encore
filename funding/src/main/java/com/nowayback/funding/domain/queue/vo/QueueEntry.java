package com.nowayback.funding.domain.queue.vo;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 대기열 엔트리 Value Object
 *
 * Value Object 특징:
 * 1. 불변 객체 (record 사용)
 * 2. 식별자는 userId (유저당 1엔트리)
 * 3. Redis에 일시적으로 저장 (DB 영속화 X)
 * 4. 상태 변경 시 새 객체 반환
 */
public record QueueEntry(
        UUID userId,              // 사용자 ID (식별자)
        UUID projectId,           // 프로젝트 ID
        QueueStatus status,       // 대기 상태
        long enqueuedAt,          // 대기열 등록 시간 (timestamp, score로 사용)
        LocalDateTime createdAt,  // 생성 시간
        LocalDateTime expiredAt   // 만료 시간 (30분)
) {

    /**
     * 새로운 대기열 엔트리 생성
     */
    public static QueueEntry create(UUID userId, UUID projectId) {
        long now = System.currentTimeMillis();
        LocalDateTime createdAt = LocalDateTime.now();

        return new QueueEntry(
                userId,
                projectId,
                QueueStatus.WAITING,
                now,
                createdAt,
                createdAt.plusMinutes(30) // 30분 후 만료
        );
    }

    /**
     * 처리 중 상태로 변경
     */
    public QueueEntry toProcessing() {
        return new QueueEntry(
                userId,
                projectId,
                QueueStatus.PROCESSING,
                enqueuedAt,
                createdAt,
                expiredAt
        );
    }

    /**
     * 완료 상태로 변경
     */
    public QueueEntry toCompleted() {
        return new QueueEntry(
                userId,
                projectId,
                QueueStatus.COMPLETED,
                enqueuedAt,
                createdAt,
                expiredAt
        );
    }

    /**
     * 실패 상태로 변경
     */
    public QueueEntry toFailed() {
        return new QueueEntry(
                userId,
                projectId,
                QueueStatus.FAILED,
                enqueuedAt,
                createdAt,
                expiredAt
        );
    }

    /**
     * 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * 대기 중 상태인지 확인
     */
    public boolean isWaiting() {
        return status == QueueStatus.WAITING;
    }
}