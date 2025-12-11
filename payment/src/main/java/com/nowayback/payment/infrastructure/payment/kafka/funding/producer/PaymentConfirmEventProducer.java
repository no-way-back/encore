package com.nowayback.payment.infrastructure.payment.kafka.funding.producer;

import com.nowayback.payment.infrastructure.payment.kafka.funding.dto.PaymentConfirmFailedEvent;
import com.nowayback.payment.infrastructure.payment.kafka.funding.dto.PaymentConfirmSucceededEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.payment-confirm-succeeded}")
    private String paymentConfirmSucceededTopic;

    @Value("${spring.kafka.topic.payment-confirm-failed}")
    private String paymentConfirmFailedTopic;

    /* 결제 승인 성공 이벤트 발행 */
    public void publishPaymentConfirmSucceededEvent(PaymentConfirmSucceededEvent event) {
        kafkaTemplate.send(paymentConfirmSucceededTopic, event.fundingId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("[Kafka Event Publisher] 결제 승인 성공 이벤트 발행 - 펀딩 ID: {}", event.fundingId());
                    } else {
                        log.error("[Kafka Event Publisher] 결제 승인 성공 이벤트 발행 실패 - 펀딩 ID: {}", event.fundingId(), ex);
                    }
                });
    }

    /* 결제 승인 실패 이벤트 발행 */
    public void publishPaymentConfirmFailedEvent(PaymentConfirmFailedEvent event) {
        kafkaTemplate.send(paymentConfirmFailedTopic, event.fundingId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("[Kafka Event Publisher] 결제 승인 실패 이벤트 발행 - 펀딩 ID: {}", event.fundingId());
                    } else {
                        log.error("[Kafka Event Publisher] 결제 승인 실패 이벤트 발행 실패 - 펀딩 ID: {}", event.fundingId(), ex);
                    }
                });
    }
}
