package com.nowayback.project.application.event;

import com.nowayback.project.domain.outbox.vo.EventPayload;
import com.nowayback.project.domain.outbox.vo.EventType;

public interface EventHandler<T extends EventPayload> {
    boolean supports(EventType eventType);
    Class<T> getPayloadType();
    void handle(T payload);
}
