package com.nowayback.funding.presentation.queue.dto.response;

import com.nowayback.funding.application.queue.dto.result.EnqueueResult;

/**
 * 대기열 등록 응답
 */
public record EnqueueResponse(
        String queueId,
        Long position,
        Long estimatedWaitTimeSeconds,
        String message
) {
    public static EnqueueResponse from(EnqueueResult result) {
        String message = String.format(
                "대기열에 등록되었습니다. 현재 %d번째 순서이며, 예상 대기 시간은 약 %d초입니다.",
                result.position(),
                result.estimatedWaitTimeSeconds()
        );

        return new EnqueueResponse(
                result.queueId(),
                result.position(),
                result.estimatedWaitTimeSeconds(),
                message
        );
    }
}