package com.nowayback.project.application.event;

import com.nowayback.project.domain.shard.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
public abstract class AsyncTransactionalEventListener<T extends DomainEvent> {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(T event) {
        try {
            log.info("[AsyncTransactionalEventListener.handleEvent]"
                    + "이벤트 처리 시작 - Type: {}, AggregateId: {}",
                event.getClass().getSimpleName(),
                event.getAggregateId()
            );

            processEvent(event);

            log.info("[AsyncTransactionalEventListener.handleEvent] 이벤트 처리 완료 - Type: {}",
                event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("[AsyncTransactionalEventListener.handleEvent] 이벤트 처리 실패 - Type: {}, Error: {}",
                event.getClass().getSimpleName(),
                e.getMessage()
            );
            handleException(event, e);
        }
    }

    protected abstract void processEvent(T event);

    protected void handleException(T event, Exception e) {
    }
}

