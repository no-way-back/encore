package com.nowayback.reward.infrastructure.kafka.constant;

public enum EventType {
    PROJECT_CREATED("PROJECT_CREATED"),
    REWARD_CREATION_SUCCESS("REWARD_CREATION_SUCCESS"),
    REWARD_CREATION_FAILED("REWARD_CREATION_FAILED"),
    FUNDING_FAILED("FUNDING_FAILED"),
    FUNDING_REFUND("FUNDING_REFUND"),
    FUNDING_COMPLETED("FUNDING_COMPLETED"),
    PROJECT_FUNDING_SUCCESS("PROJECT_FUNDING_SUCCESS"),
    UNSUPPORTED_TYPE("ONLY_TEST");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}