package com.nowayback.project.application.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.project.domain.outbox.vo.EventPayload;
import com.nowayback.project.domain.outbox.vo.EventType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventDispatcher {

    private final List<EventHandler<? extends EventPayload>> handlers;
    private final ObjectMapper objectMapper;

    public void dispatch(EventType eventType, String payload) {
        handlers.forEach(handler -> {
            if (handler.supports(eventType)) {
                handleWithCorrectType(handler, payload);
            }
        });
    }

    private <T extends EventPayload> void handleWithCorrectType(
        EventHandler<T> processor,
        String payload
    ) {
        try {
            processor.handle(objectMapper.readValue(payload, processor.getPayloadType()));
        } catch (Exception e) {
            throw new RuntimeException("이벤트 처리 중 오류", e);
        }
    }
}