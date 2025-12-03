package com.nowayback.reward.infrastructure.kafka.publisher;

import com.nowayback.reward.infrastructure.kafka.dto.project.event.RewardCreationResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.reward-creation-result}")
    private String rewardResultTopic;

    /**
     * 리워드 성공 여부 이벤트 발행
     */
    public void rewardCreationResult(RewardCreationResultEvent event) {
        kafkaTemplate.send(rewardResultTopic, event.payload().projectId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("리워드 결과 발행 - 프로젝트: {}, 성공: {}",
                                event.payload().projectId(), event.payload().success());
                    } else {
                        log.error("리워드 결과 발행 실패 - 프로젝트: {}",
                                event.payload().projectId(), ex);
                    }
                });
    }
}
