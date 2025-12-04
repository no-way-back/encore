package com.nowayback.project.infrastructure.messaging.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 프로젝트 → 리워드 / 펀딩 생성 요청
    public static final String PROJECT_REWARD_CREATION = "project-reward-creation";
    public static final String PROJECT_FUNDING_CREATION = "project-funding-creation";

    // 리워드 생성 결과
    public static final String REWARD_CREATED = "reward-created";
    public static final String REWARD_CREATION_FAILED = "reward-creation-failed";
    public static final String REWARD_CREATION_RESULT = "reward-creation-result";
    // 펀딩 생성 결과
    public static final String FUNDING_CREATED = "funding-created";
    public static final String FUNDING_CREATION_FAILED = "project-funding-created-failed";

    // 프로젝트 최종 상태
    public static final String PROJECT_ACTIVATED = "project-activated";
    public static final String PROJECT_CREATION_FAILED = "project-creation-failed";
}
