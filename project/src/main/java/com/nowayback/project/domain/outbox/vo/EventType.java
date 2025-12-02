package com.nowayback.project.domain.outbox.vo;

public enum EventType {
    PROJECT_DRAFT_SUBMITTED,

    REWARD_CREATE_REQUESTED,
    FUNDING_CREATE_REQUESTED,

    // Saga 단계별 완료 이벤트
    REWARD_CREATED,
    REWARD_CREATION_FAILED,
    FUNDING_CREATED,
    FUNDING_CREATION_FAILED,

    // 최종 이벤트
    PROJECT_ACTIVATED,
    PROJECT_CREATION_FAILED;

    public String getTopic() {
        return this.name().toLowerCase().replace("_", "-");
    }
}
