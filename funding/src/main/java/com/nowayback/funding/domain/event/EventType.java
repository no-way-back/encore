package com.nowayback.funding.domain.event;

import lombok.Getter;

@Getter
public enum EventType {
    FUNDING_PAYMENT_PROCESS("funding-payment-process"),
    FUNDING_COMPLETED("funding-completed"),
    FUNDING_FAILED("funding-failed"),
    FUNDING_REFUND("funding-refund"),
    PROJECT_FUNDING_SUCCESS("project-funding-success"),
    PROJECT_FUNDING_FAILED("project-funding-failed"),
    PROJECT_FUNDING_CREATED_FAILED("project-funding-creation-failed");

    private final String topicName;

    EventType(String topicName) {
        this.topicName = topicName;
    }
}