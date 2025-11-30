package com.nowayback.project.application.outbox.event;

import com.nowayback.project.domain.outbox.Outbox;
import lombok.Getter;

@Getter
public class OutboxEvent {
    private Outbox outbox;

    public static OutboxEvent of(Outbox outbox) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.outbox = outbox;

        return outboxEvent;
    }
}
