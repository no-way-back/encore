package com.nowayback.project.infrastructure.messaging.kafka;

import com.nowayback.project.domain.outbox.vo.EventType;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicMapper {

    public String map(EventType eventType) {

        return switch (eventType) {
            case PROJECT_REWARD_CREATION ->
                KafkaTopics.PROJECT_REWARD_CREATION;
            case PROJECT_FUNDING_CREATION ->
                KafkaTopics.FUNDING_CREATE_CREATION;
            case REWARD_CREATED ->
                KafkaTopics.REWARD_CREATED;
            case REWARD_CREATION_FAILED ->
                KafkaTopics.REWARD_CREATION_FAILED;
            case FUNDING_CREATED ->
                KafkaTopics.FUNDING_CREATED;
            case FUNDING_CREATION_FAILED ->
                KafkaTopics.FUNDING_CREATION_FAILED;
            case PROJECT_ACTIVATED ->
                KafkaTopics.PROJECT_ACTIVATED;
            case PROJECT_CREATION_FAILED ->
                KafkaTopics.PROJECT_CREATION_FAILED;
            default ->
                throw new IllegalArgumentException(
                    "KafkaTopicMapper: 매핑되지 않은 EventType입니다. eventType=" + eventType.name()
                );
        };
    }
}
