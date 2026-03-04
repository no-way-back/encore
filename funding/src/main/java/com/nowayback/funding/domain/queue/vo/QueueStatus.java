package com.nowayback.funding.domain.queue.vo;

public enum QueueStatus {
    WAITING,      // 대기 중
    PROCESSING,   // 처리 중
    COMPLETED,    // 완료
    FAILED,       // 실패
    EXPIRED       // 만료 (타임아웃)
}