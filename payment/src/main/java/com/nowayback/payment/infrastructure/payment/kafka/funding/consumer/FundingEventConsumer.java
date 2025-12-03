package com.nowayback.payment.infrastructure.payment.kafka.funding.consumer;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import com.nowayback.payment.infrastructure.payment.kafka.funding.dto.ProjectFundingFailedEvent;
import com.nowayback.payment.infrastructure.payment.kafka.payment.dto.RefundRequestEvent;
import com.nowayback.payment.infrastructure.payment.kafka.payment.producer.RefundEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundingEventConsumer {

    private final PaymentRepository paymentRepository;
    private final RefundEventPublisher refundEventPublisher;

    @KafkaListener(
            topics = "${spring.kafka.topic.project-funding-failed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeProjectFundingFailedEvent(
            ProjectFundingFailedEvent event
    ) {
        log.info("[Kafka Event Listener] 이벤트 수신 - 프로젝트 펀딩 실패: {}", event.projectId());

        try {
            List<Payment> payments = paymentRepository.findAllCompletedByProjectId(ProjectId.of(event.projectId()));

            if (payments.isEmpty()) {
                log.info("[Kafka Event Listener] 환불 처리 대상 결제 내역이 없습니다 - 프로젝트 ID: {}", event.projectId());
                return;
            }

            log.info("[Kafka Event Listener] 환불 처리 대상 결제 내역 수: {} - 프로젝트 ID: {}", payments.size(), event.projectId());

            for (Payment payment : payments) {
                RefundRequestEvent refundEvent = new RefundRequestEvent(
                        payment.getId(),
                        event.projectId(),
                        "프로젝트 펀딩 실패로 인한 환불 처리",
                        LocalDateTime.now()
                );

                refundEventPublisher.publish(refundEvent);
            }

            log.info("[Kafka Event Listener] 환불 요청 이벤트 발행 완료 - 프로젝트 ID: {}", event.projectId());
        } catch (Exception e) {
            log.error("[Kafka Event Listener] 프로젝트 펀딩 실패 처리 중 오류 발생 - 프로젝트 ID: {}, 오류: {}",
                    event.projectId(), e.getMessage());
        }
    }
}
