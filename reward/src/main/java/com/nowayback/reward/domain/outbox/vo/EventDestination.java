package com.nowayback.reward.domain.outbox.vo;

import lombok.Getter;

@Getter
public enum EventDestination {
    PROJECT_SERVICE("reward-creation-result");

    private final String topicName;

    EventDestination(String topicName) {
        this.topicName = topicName;
    }

}