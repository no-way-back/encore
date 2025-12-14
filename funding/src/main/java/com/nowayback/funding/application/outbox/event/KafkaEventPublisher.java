package com.nowayback.funding.application.outbox.event;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nowayback.funding.domain.outbox.entity.Outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publish(Outbox outbox) throws Exception {
		String topic = outbox.getEventType();
		String key = outbox.getAggregateId().toString();

		kafkaTemplate.send(topic, key, outbox.getPayload())
			.get(3, TimeUnit.SECONDS);

		log.debug("이벤트 발행 완료 - topic={}, key={}, eventId={}",
			topic, key, outbox.getId());
	}
}
