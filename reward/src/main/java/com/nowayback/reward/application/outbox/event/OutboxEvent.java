package com.nowayback.reward.application.outbox.event;

import com.nowayback.reward.domain.outbox.Outbox;

public record OutboxEvent(Outbox outbox) {
    public static OutboxEvent of(Outbox outbox) {
        return new OutboxEvent(outbox);
    }
}