package com.nowayback.reward.application.inbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import com.nowayback.reward.domain.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboxProcessor {

    private final InboxRepository inboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * 인박스 패턴 핵심 메서드
     * 1. 이벤트를 DB에 먼저 저장 (중복 체크)
     * 2. 비즈니스 로직 실행
     * 3. 처리 완료 상태로 변경
     * 모두 단일 트랜잭션으로 처리
     */
    @Transactional
    public <T> void processEvent(
            UUID eventId,
            EventType eventType,
            UUID aggregateId,
            T eventPayload,
            Consumer<T> businessLogic
    ) {
        String payload = convertToJson(eventPayload);

        Inbox inbox;
        try {
            inbox = Inbox.create(eventId, eventType, aggregateId, payload);
            inbox = inboxRepository.save(inbox);

            log.info("인박스 이벤트 저장 완료 - eventId: {}, type: {}, aggregateId: {}",
                    eventId, eventType, aggregateId);

        } catch (DataIntegrityViolationException e) {
            log.info("이미 처리된 이벤트 - eventId: {}, type: {}", eventId, eventType);
            return; // 중복 이벤트는 무시
        }

        if (inbox.isProcessed()) {
            log.info("이미 처리 완료된 이벤트 - eventId: {}", eventId);
            return;
        }

        try {
            businessLogic.accept(eventPayload);

            inbox.markAsProcessed();

            log.info("이벤트 처리 완료 - eventId: {}, type: {}", eventId, eventType);

        } catch (Exception e) {
            inbox.incrementRetryCount();

            log.error("이벤트 처리 실패 - eventId: {}, type: {}, retryCount: {}, error: {}",
                    eventId, eventType, inbox.getRetryCount(), e.getMessage(), e);

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
