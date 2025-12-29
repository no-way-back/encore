package com.nowayback.funding.application.outbox.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public void publish(Outbox outbox) throws Exception {
		String topic = outbox.getEventType().getTopicName();
		String key = outbox.getAggregateId().toString();

		Object payload = objectMapper.readValue(
				outbox.getPayload(),
				Class.forName(outbox.getPayloadType())
		);

		kafkaTemplate.send(topic, key, payload)
				.get(3, TimeUnit.SECONDS);

		log.debug("이벤트 발행 완료 - topic={}, key={}, eventId={}",
				topic, key, outbox.getId());
	}
}