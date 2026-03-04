package com.nowayback.funding.application.queue.dto.result;

/**
 * 대기열 등록 결과
 */
public record EnqueueResult(
        String queueId,
        Long position,
        Long estimatedWaitTimeSeconds
) {
    public static EnqueueResult of(String queueId, Long position, Long estimatedWaitTimeSeconds) {
        return new EnqueueResult(queueId, position, estimatedWaitTimeSeconds);
    }
}