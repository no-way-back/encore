package com.nowayback.funding.infrastructure.messaging.kafka;

import static com.nowayback.funding.infrastructure.config.KafkaTopics.*;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.funding.domain.service.ProjectEventPort;
import com.nowayback.funding.infrastructure.messaging.kafka.dto.ProjectCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectEventAdapter {

	private final ProjectEventPort projectEventPort;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = PROJECT_CREATED, groupId = "${spring.kafka.consumer.group-id}")
	public void onProjectCreated(String message, Acknowledgment ack) {
		try {
			log.info("프로젝트 생성 이벤트 수신");

			ProjectCreatedEvent event = objectMapper.readValue(message, ProjectCreatedEvent.class);

			projectEventPort.handleProjectCreated(
				event.projectId(),
				event.targetAmount(),
				event.startDate(),
				event.endDate()
			);

			ack.acknowledge();

		} catch (Exception e) {
			log.error("프로젝트 생성 이벤트 처리 실패: {}", message, e);
		}
	}
}
