package com.nowayback.payment.infrastructure.payment.kafka.funding.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.payment.application.payment.PaymentService;
import com.nowayback.payment.application.payment.dto.command.CreatePaymentCommand;
import com.nowayback.payment.domain.jobs.entity.RefundJob;
import com.nowayback.payment.domain.jobs.repository.RefundJobRepository;
import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import com.nowayback.payment.infrastructure.payment.kafka.funding.dto.FundingPaymentProcessEvent;
import com.nowayback.payment.infrastructure.payment.kafka.funding.dto.ProjectFundingFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundingEventConsumer {

    private final ObjectMapper objectMapper;

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final RefundJobRepository refundJobRepository;

    private static final String REFUND_REASON_PROJECT_FUNDING_FAILED = "프로젝트 펀딩 실패에 따른 환불";

    @KafkaListener(
            topics = "${spring.kafka.topic.funding-payment-process}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeFundingPaymentProcessEvent(
            String message,
            Acknowledgment ack
    ) {
        log.info("[Kafka Event Listener] 이벤트 수신 - 펀딩 결제 생성 처리: {}", message);

        try {
            String json = objectMapper.readValue(message, String.class);
            FundingPaymentProcessEvent event = objectMapper.readValue(json, FundingPaymentProcessEvent.class);

            paymentService.createPayment(
                    CreatePaymentCommand.of(
                            event.userId(),
                            event.fundingId(),
                            event.projectId(),
                            event.amount()
                    )
            );
            ack.acknowledge();

            log.info("[Kafka Event Listener] 펀딩 결제 생성 처리 완료: {}", event.fundingId());
        } catch (Exception e) {
            log.error("[Kafka Event Listener] 펀딩 결제 생성 처리 중 오류 발생 - 메세지: {}, 오류: {}",
                    message, e.getMessage());
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.project-funding-failed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeProjectFundingFailedEvent(
            String message,
            Acknowledgment ack
    ) {
        log.info("[Kafka Event Listener] 이벤트 수신 - 프로젝트 펀딩 실패: {}", message);

        try {
            String json = objectMapper.readValue(message, String.class);
            ProjectFundingFailedEvent event = objectMapper.readValue(json, ProjectFundingFailedEvent.class);

            List<Payment> payments = paymentRepository.findAllCompletedByProjectId(ProjectId.of(event.projectId()));

            if (payments.isEmpty()) {
                log.info("[Kafka Event Listener] 환불 처리 대상 결제 내역이 없습니다 - 프로젝트 ID: {}", event.projectId());
                return;
            }

            log.info("[Kafka Event Listener] 환불 처리 대상 결제 내역 수: {} - 프로젝트 ID: {}", payments.size(), event.projectId());

            List<RefundJob> jobs = payments.stream()
                    .map(payment ->
                            RefundJob.create(payment.getId(), payment.getProjectId().getId(), REFUND_REASON_PROJECT_FUNDING_FAILED)).toList();

            refundJobRepository.saveAll(jobs);
            ack.acknowledge();

            log.info("[Kafka Event Listener] 환불 요청 이벤트 발행 완료 - 프로젝트 ID: {}", event.projectId());
        } catch (Exception e) {
            log.error("[Kafka Event Listener] 프로젝트 펀딩 실패 처리 중 오류 발생 - 메세지: {}, 오류: {}",
                    message, e.getMessage());
        }
    }
}
