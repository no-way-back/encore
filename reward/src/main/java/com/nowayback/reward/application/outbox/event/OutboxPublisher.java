package com.nowayback.reward.application.outbox.event;

import com.nowayback.reward.domain.outbox.entity.Outbox;

/**
 * Outbox 발행 인터페이스
 * Application 계층에서 정의, Infrastructure 계층에서 구현
 */
public interface OutboxPublisher {

    /**
     * 즉시 발행 시도 (트랜잭션 커밋 직후)
     */
    void publishImmediately(Outbox outbox);

    /**
     * 재시도 발행 (폴링 스케줄러에서 호출)
     */
    boolean publishWithRetry(Outbox outbox);
}