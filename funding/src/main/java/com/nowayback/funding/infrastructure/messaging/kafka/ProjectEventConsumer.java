package com.nowayback.funding.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.funding.application.fundingProjectStatistics.dto.event.ProjectFundingCreationFailedEvent;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.infrastructure.messaging.kafka.dto.ProjectCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static com.nowayback.funding.infrastructure.config.KafkaConsumerTopics.PROJECT_FUNDING_CREATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectEventConsumer {

	private final FundingProjectStatisticsService fundingProjectStatisticsService;
	private final OutboxService outboxService;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = PROJECT_FUNDING_CREATION, groupId = "${spring.kafka.consumer.group-id}")
	public void onProjectCreated(String message, Acknowledgment ack) {

		ProjectCreatedEvent event = null;

		try {
			log.info("프로젝트 생성 이벤트 수신");

			event = objectMapper.readValue(message, ProjectCreatedEvent.class);

			fundingProjectStatisticsService.createProjectStatistics(
				event.projectId(),
				event.creatorId(),
				event.targetAmount(),
				event.startDate(),
				event.endDate()
			);

			ack.acknowledge();
			log.info("프로젝트 통계 생성 완료 - projectId: {}", event.projectId());

		} catch (Exception e) {
			log.error("프로젝트 통계 생성 실패 - projectId: {}, error: {}",
				event != null ? event.projectId() : "unknown",
				e.getMessage(), e);

			if (event != null) {
				ProjectFundingCreationFailedEvent failedEvent = ProjectFundingCreationFailedEvent.of(
						event.projectId(),
						event.creatorId(),
						event.targetAmount(),
						event.startDate(),
						event.endDate(),
						e.getMessage()
				);
				outboxService.publish(failedEvent);

				log.info("프로젝트 생성 실패 보상 이벤트 발행 - projectId: {}", event.projectId());
			}

			ack.acknowledge();
		}
	}
}
