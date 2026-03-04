package com.nowayback.funding.presentation.queue.dto.response;

import com.nowayback.funding.application.queue.dto.result.QueuePositionResult;

/**
 * 대기 순번 조회 응답
 */
public record QueuePositionResponse(
        Long position,
        Long totalWaiting,
        Long estimatedWaitTimeSeconds,
        String status,
        String message
) {
    public static QueuePositionResponse from(QueuePositionResult result) {
        String message = switch (result.status()) {
            case "WAITING" -> String.format(
                    "현재 %d번째 대기 중입니다. (전체 %d명 대기, 예상 대기 시간: %d초)",
                    result.position(),
                    result.totalWaiting(),
                    result.estimatedWaitTimeSeconds()
            );
            case "PROCESSING" -> "현재 처리 중입니다. 잠시만 기다려주세요.";
            case "NOT_FOUND" -> "대기열에 등록되지 않았습니다.";
            default -> "알 수 없는 상태입니다.";
        };

        return new QueuePositionResponse(
                result.position(),
                result.totalWaiting(),
                result.estimatedWaitTimeSeconds(),
                result.status(),
                message
        );
    }
}