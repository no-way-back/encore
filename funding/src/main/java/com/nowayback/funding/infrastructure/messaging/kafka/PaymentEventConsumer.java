package com.nowayback.funding.infrastructure.messaging.kafka;

import static com.nowayback.funding.domain.event.FundingProducerTopics.*;
import static com.nowayback.funding.infrastructure.config.KafkaConsumerTopics.*;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.infrastructure.messaging.kafka.dto.PaymentFailureEvent;
import com.nowayback.funding.infrastructure.messaging.kafka.dto.PaymentSuccessEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

	private final FundingService fundingService;
	private final OutboxService outboxService;
	private final ObjectMapper objectMapper;

	/**
	 * 결제 성공 이벤트 처리
	 */
	@KafkaListener(
		topics = FUNDING_PAYMENT_SUCCESS,
		groupId = "${spring.kafka.consumer.group-id}"
	)
	public void onPaymentSuccess(String message, Acknowledgment ack) throws Exception {

		log.info("결제 성공 이벤트 수신");

		PaymentSuccessEvent event = objectMapper.readValue(message, PaymentSuccessEvent.class);

		Funding funding = fundingService.completeFunding(event.fundingId(), event.paymentId());

		if (funding.hasReservation()) {
			publishFundingCompletedEvent(event, funding);
		}

		ack.acknowledge();

		log.info("결제 성공 처리 완료 - fundingId: {}, paymentId: {}, hasReservation: {}",
			event.fundingId(), event.paymentId(), funding.hasReservation());
	}

	/**
	 * 결제 실패 이벤트 처리
	 * 실패 시 자동 재시도 (예외가 자동으로 던져짐)
	 */
	@KafkaListener(
		topics = FUNDING_PAYMENT_FAILURE,
		groupId = "${spring.kafka.consumer.group-id}"
	)
	public void onPaymentFailure(String message, Acknowledgment ack) throws Exception {

		log.info("결제 실패 이벤트 수신");

		PaymentFailureEvent event = objectMapper.readValue(message, PaymentFailureEvent.class);

		Funding funding = fundingService.failFunding(event.fundingId());

		if (funding.hasReservation()) {
			publishFundingFailedEvent(event, funding);
			log.info("재고 복구 이벤트 발행 - fundingId: {}, reservationIds: {}",
				event.fundingId(), funding.getReservationIds());
		} else {
			log.info("순수 후원 - 재고 복구 불필요 - fundingId: {}", event.fundingId());
		}

		ack.acknowledge();
	}

	/**
	 * 펀딩 완료 이벤트 발행 (Reward 서비스로 QR 생성 요청)
	 */
	private void publishFundingCompletedEvent(PaymentSuccessEvent event, Funding funding) {
		outboxService.publishSuccessEvent(
			"FUNDING",
			funding.getId(),
			FUNDING_COMPLETED,
			Map.of(
				"fundingId", funding.getId(),
				"userId", funding.getUserId(),
				"projectId", funding.getProjectId(),
				"reservationId", funding.getReservationIds(),
				"amount", funding.getAmount()
			)
		);

		log.info("펀딩 완료 이벤트 발행 완료 - fundingId: {}", event.fundingId());
	}

	/**
	 * 펀딩 실패 이벤트 발행 (Reward 재고 복구)
	 */
	private void publishFundingFailedEvent(PaymentFailureEvent event, Funding funding) {
		outboxService.publishCompensationEvent(
			"FUNDING",
			event.fundingId(),
			FUNDING_FAILED,
			Map.of(
				"fundingId", event.fundingId(),
				"projectId", funding.getProjectId(),
				"userId", funding.getUserId(),
				"reservationIds", funding.getReservationIds()
			)
		);

		log.info("펀딩 실패 보상 이벤트 발행 완료 - fundingId: {}, reservationIds: {}",
			event.fundingId(), funding.getReservationIds());
	}
}