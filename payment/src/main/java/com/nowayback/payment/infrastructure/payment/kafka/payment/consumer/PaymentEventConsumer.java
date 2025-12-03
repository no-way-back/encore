package com.nowayback.payment.infrastructure.payment.kafka.payment.consumer;

import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.payment.dto.command.RefundPaymentCommand;
import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.infrastructure.payment.kafka.payment.dto.RefundRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${spring.kafka.topic.payment-refund-requested}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "5"
    )
    public void handleRefundRequest(RefundRequestEvent event) {
        log.info("[Kafka Event Listener] 이벤트 수신 - 환불 요청: 결제 ID: {}, 프로젝트 ID: {}",
                event.paymentId(), event.projectId());

        try {
            RefundPaymentCommand command = RefundPaymentCommand.of(
                    event.paymentId(),
                    event.reason(),
                    null, null, null
            );

            PaymentResult result = paymentService.refundPayment(command);

            log.info("[Kafka Event Listener] 환불 처리 완료 - 결제 ID: {}, 프로젝트 ID: {}, 환불 상태: {}",
                    event.paymentId(), event.projectId(), result.status());
        } catch (Exception e) {
            log.error("[Kafka Event Listener] 환불 처리 중 오류 발생 - 결제 ID: {}, 프로젝트 ID: {}, 오류: {}",
                    event.paymentId(), event.projectId(), e.getMessage());
        }
    }
}
