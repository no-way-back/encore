package com.nowayback.funding.presentation.test;

import static com.nowayback.funding.infrastructure.config.KafkaConsumerTopics.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nowayback.funding.infrastructure.messaging.kafka.dto.ProjectCreatedEvent;

@RestController
@RequestMapping("/test/events")
@Profile({"local"})
@RequiredArgsConstructor
@Slf4j
public class MockEventController {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@PostMapping("/project-created")
	public ResponseEntity<Map<String, Object>> publishProjectCreatedEvent(
		@RequestBody(required = false) MockProjectCreatedRequest request
	) {
		try {
			if (request == null) {
				request = MockProjectCreatedRequest.createRandom();
			}

			ProjectCreatedEvent event = new ProjectCreatedEvent(
				request.projectId(),
				request.creatorId(),
				request.targetAmount(),
				request.startDate(),
				request.endDate()
			);

			kafkaTemplate.send(PROJECT_FUNDING_CREATION, event);

			log.info("Mock 프로젝트 생성 이벤트 발행 - projectId: {}", event.projectId());

			return ResponseEntity.ok(Map.of(
				"status", "success",
				"message", "Mock event published",
				"event", event
			));

		} catch (Exception e) {
			log.error("Mock 이벤트 발행 실패", e);
			return ResponseEntity.status(500).body(Map.of(
				"status", "error",
				"message", e.getMessage()
			));
		}
	}

	@PostMapping("/project-created/bulk")
	public ResponseEntity<Map<String, Object>> publishBulkProjectCreatedEvents(
		@RequestParam(defaultValue = "10") int count
	) {
		try {
			List<UUID> projectIds = new ArrayList<>();

			for (int i = 0; i < count; i++) {
				MockProjectCreatedRequest request = MockProjectCreatedRequest.createRandom();

				ProjectCreatedEvent event = new ProjectCreatedEvent(
					request.projectId(),
					request.creatorId(),
					request.targetAmount(),
					request.startDate(),
					request.endDate()
				);

				kafkaTemplate.send(PROJECT_FUNDING_CREATION, event);
				projectIds.add(event.projectId());
			}

			log.info("Mock 프로젝트 생성 이벤트 {}개 발행", count);

			return ResponseEntity.ok(Map.of(
				"status", "success",
				"message", count + " mock events published",
				"projectIds", projectIds
			));

		} catch (Exception e) {
			log.error("Bulk mock 이벤트 발행 실패", e);
			return ResponseEntity.status(500).body(Map.of(
				"status", "error",
				"message", e.getMessage()
			));
		}
	}
}