package com.nowayback.funding.application.queue.dto.result;

/**
 * 대기 순번 조회 결과
 */
public record QueuePositionResult(
        Long position,
        Long totalWaiting,
        Long estimatedWaitTimeSeconds,
        String status  // WAITING, PROCESSING, NOT_FOUND
) {
    public static QueuePositionResult waiting(Long position, Long totalWaiting, Long estimatedWaitTimeSeconds) {
        return new QueuePositionResult(position, totalWaiting, estimatedWaitTimeSeconds, "WAITING");
    }

    public static QueuePositionResult processing() {
        return new QueuePositionResult(0L, 0L, 0L, "PROCESSING");
    }

    public static QueuePositionResult notFound() {
        return new QueuePositionResult(null, null, null, "NOT_FOUND");
    }
}