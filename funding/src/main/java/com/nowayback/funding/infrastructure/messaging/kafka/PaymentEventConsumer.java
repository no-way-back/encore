package com.nowayback.funding.infrastructure.messaging.kafka;

import static com.nowayback.funding.infrastructure.config.KafkaConsumerTopics.*;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.funding.application.funding.dto.event.FundingCompletedEvent;
import com.nowayback.funding.application.funding.dto.event.FundingFailedEvent;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.infrastructure.messaging.kafka.dto.PaymentFailureEvent;
import com.nowayback.funding.infrastructure.messaging.kafka.dto.PaymentSuccessEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final FundingService fundingService;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = PAYMENT_CONFIRM_SUCCEEDED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentSuccess(String message, Acknowledgment ack) {
        PaymentSuccessEvent event = null;

        try {
            log.info("결제 성공 이벤트 수신");

            event = objectMapper.readValue(message, PaymentSuccessEvent.class);

            Funding funding = fundingService.completeFunding(event.fundingId(), event.paymentId());

            if (funding.hasReservation()) {
                publishFundingCompletedEvent(funding);
            }

            log.info("결제 성공 처리 완료 - fundingId: {}, paymentId: {}, hasReservation: {}",
                    event.fundingId(), event.paymentId(), funding.hasReservation());

        } catch (Exception e) {
            log.error("결제 성공 이벤트 처리 중 오류 발생 - fundingId: {}, error: {}",
                    event != null ? event.fundingId() : "unknown",
                    e.getMessage(), e);

        } finally {
            ack.acknowledge();
        }
    }

    @KafkaListener(
            topics = PAYMENT_CONFIRM_FAILED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentFailure(String message, Acknowledgment ack) {
        PaymentFailureEvent event = null;

        try {
            log.info("결제 실패 이벤트 수신");

            event = objectMapper.readValue(message, PaymentFailureEvent.class);

            Funding funding = fundingService.failFunding(event.fundingId());

            if (funding.hasReservation()) {
                publishFundingFailedEvent(funding.getId());
                log.info("재고 복구 이벤트 발행 - fundingId: {}", funding.getId());
            } else {
                log.info("순수 후원 - 재고 복구 불필요 - fundingId: {}", funding.getId());
            }

        } catch (Exception e) {
            log.error("결제 실패 이벤트 처리 중 오류 발생 - fundingId: {}, error: {}",
                    event != null ? event.fundingId() : "unknown",
                    e.getMessage(), e);

        } finally {
            ack.acknowledge();
        }
    }

    /**
     * 펀딩 완료 이벤트 발행 (Reward 서비스로 QR 생성 요청)
     */
    private void publishFundingCompletedEvent(Funding funding) {
        outboxService.publishFundingCompletedEvent(
                FundingCompletedEvent.from(funding)
        );

        log.info("펀딩 완료 이벤트 발행 완료 - fundingId: {}", funding.getId());
    }

    /**
     * 펀딩 실패 이벤트 발행 (Reward 재고 복구)
     */
    private void publishFundingFailedEvent(UUID fundingId) {
        outboxService.publishFundingFailedEvent(
                FundingFailedEvent.from(fundingId)
        );

        log.info("펀딩 실패 보상 이벤트 발행 완료 - fundingId: {}", fundingId);
    }
}