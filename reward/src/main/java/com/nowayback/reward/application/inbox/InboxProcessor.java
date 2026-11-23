package com.nowayback.reward.application.inbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import com.nowayback.reward.domain.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * 인박스 패턴으로 같은 이벤트를 한 번만 처리한다.
 *
 * 1. 수신 기록을 별도 트랜잭션으로 먼저 커밋한다 (INSERT ... ON CONFLICT DO NOTHING)
 *    - 이미 PROCESSED인 이벤트면 여기서 끝낸다
 *    - RECEIVED/FAILED면 재전송된 이벤트로 보고 다시 처리한다
 * 2. 비즈니스 로직과 "처리 완료" 표시를 한 트랜잭션에서 실행한다
 *    - 인박스 행을 잠그고 상태를 다시 확인해, 같은 이벤트가 동시에 들어와도 한 번만 처리된다
 * 3. 실패하면 별도 트랜잭션으로 실패를 기록하고 예외를 다시 던진다
 *    - 본 작업은 롤백되지만 실패 이력은 남고, Kafka 에러 핸들러가 재시도한다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboxProcessor {

    private final InboxRepository inboxRepository;
    private final InboxStatusUpdater inboxStatusUpdater;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;

    public <T> void processEvent(
            UUID eventId,
            EventType eventType,
            UUID aggregateId,
            T eventPayload,
            Consumer<T> businessLogic
    ) {
        String payload = convertToJson(eventPayload);

        boolean firstReceived = inboxStatusUpdater.recordReceived(
                Inbox.create(eventId, eventType, aggregateId, payload)
        );

        if (firstReceived) {
            log.info("인박스 이벤트 저장 완료 - eventId: {}, type: {}, aggregateId: {}",
                    eventId, eventType, aggregateId);
        } else {
            log.info("이미 수신한 이벤트 - eventId: {}, type: {}", eventId, eventType);
        }

        try {
            Boolean processed = transactionTemplate.execute(status -> {
                Inbox inbox = inboxRepository.findByIdForUpdate(eventId)
                        .orElseThrow(() -> new IllegalStateException("인박스 이벤트가 없음 - eventId: " + eventId));

                if (inbox.isProcessed()) {
                    return false;
                }

                businessLogic.accept(eventPayload);
                inbox.markAsProcessed();
                return true;
            });

            if (Boolean.TRUE.equals(processed)) {
                log.info("이벤트 처리 완료 - eventId: {}, type: {}", eventId, eventType);
            } else {
                log.info("이미 처리 완료된 이벤트 - eventId: {}", eventId);
            }

        } catch (RuntimeException e) {
            inboxStatusUpdater.markAsFailed(eventId, e.getMessage());
            log.error("이벤트 처리 실패 - eventId: {}, type: {}, error: {}",
                    eventId, eventType, e.getMessage(), e);
            throw e;
        }
    }

    private <T> String convertToJson(T payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }
}
