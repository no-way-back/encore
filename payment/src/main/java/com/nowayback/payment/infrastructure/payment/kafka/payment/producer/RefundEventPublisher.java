package com.nowayback.payment.infrastructure.payment.kafka.payment.producer;

import com.nowayback.payment.infrastructure.payment.kafka.payment.dto.RefundRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.payment-refund-requested}")
    private String refundRequestTopic;

    public void publish(RefundRequestEvent event) {
        try {
            kafkaTemplate.send(refundRequestTopic, event.paymentId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("[Kafka Event Publisher] 환불 요청 이벤트 발행 성공 - 결제 ID: {}, 프로젝트 ID: {}",
                                    event.paymentId(), event.projectId());
                        } else {
                            log.error("[Kafka Event Publisher] 환불 요청 이벤트 발행 실패 - 결제 ID: {}, 프로젝트 ID: {}",
                                    event.paymentId(), event.projectId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("[Kafka Event Publisher] 환불 요청 이벤트 발행 중 오류 발생 - 결제 ID: {}, 프로젝트 ID: {}, 오류: {}",
                    event.paymentId(), event.projectId(), e.getMessage());

        }
    }
}
