package com.nowayback.reward.infrastructure.kafka.listener;

import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingCompletedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingFailedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingRefundEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.ProjectFundingSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.nowayback.reward.domain.vo.EventType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundingEventListener {

    private final RewardStockService rewardStockService;
    private final QRCodeService qrCodeService;
    private final InboxProcessor inboxProcessor;

    @KafkaListener(
            topics = "${spring.kafka.topic.funding-failed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "fundingFailedListenerFactory"
    )
    public void consumeFundingFailedEvent(
            @Payload FundingFailedEvent event,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("결제 실패 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                    event.eventId(), event.eventType(), event.payload().fundingId());

            if (validateEventType(FUNDING_FAILED, event.eventType(), acknowledgment)) {
                return;
            }

            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().fundingId(),
                    event.payload(),
                    payload -> rewardStockService.restoreStock(payload.fundingId())
            );

            acknowledgment.acknowledge();

            log.info("결제 실패 재고 복원 완료 - 펀딩: {}", event.payload().fundingId());

        } catch (Exception e) {
            log.error("결제 실패 이벤트 처리 실패 - 에러 핸들러가 재시도 처리", e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.funding-refund}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "fundingRefundListenerFactory"
    )
    public void consumeFundingRefundEvent(
            @Payload FundingRefundEvent event,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("펀딩 환불 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                    event.eventId(), event.eventType(), event.payload().fundingId());

            if (validateEventType(FUNDING_REFUND, event.eventType(), acknowledgment)) {
                return;
            }

            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().fundingId(),
                    event.payload(),
                    payload -> rewardStockService.restoreStock(payload.fundingId())
            );

            acknowledgment.acknowledge();

            log.info("펀딩 환불 재고 복원 완료 - 펀딩: {}", event.payload().fundingId());

        } catch (Exception e) {
            log.error("펀딩 환불 이벤트 처리 실패 - 에러 핸들러가 재시도 처리", e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.funding-completed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "fundingCompletedListenerFactory"
    )
    public void consumeFundingCompletedEvent(
            @Payload FundingCompletedEvent event,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("펀딩 완료 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                    event.eventId(), event.eventType(), event.payload().fundingId());

            if (validateEventType(FUNDING_COMPLETED, event.eventType(), acknowledgment)) {
                return;
            }

            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().fundingId(),
                    event.payload(),
                    payload -> {
                        rewardStockService.confirmReservations(payload.fundingId());
                        qrCodeService.createQRCode(payload.toCommand());
                    }
            );

            acknowledgment.acknowledge();

            log.info("재고 확정 및 QR 코드 생성 완료 - 펀딩: {}", event.payload().fundingId());

        } catch (Exception e) {
            log.error("펀딩 완료 이벤트 처리 실패 - 에러 핸들러가 재시도 처리", e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.project-funding-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "projectFundingSuccessListenerFactory"
    )
    public void consumeProjectFundingSuccessEvent(
            @Payload ProjectFundingSuccessEvent event,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("프로젝트 펀딩 성공 이벤트 수신 - ID: {}, 타입: {}, 프로젝트: {}",
                    event.eventId(), event.eventType(), event.payload().projectId());

            if (validateEventType(PROJECT_FUNDING_SUCCESS, event.eventType(), acknowledgment)) {
                return;
            }

            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().projectId(),
                    event.payload(),
                    payload -> qrCodeService.sendQRCodesByProject(payload.projectId())
            );

            acknowledgment.acknowledge();

            log.info("QR 코드 이메일 발송 완료 - 프로젝트: {}", event.payload().projectId());

        } catch (Exception e) {
            log.error("프로젝트 펀딩 성공 이벤트 처리 실패 - 에러 핸들러가 재시도 처리", e);
            throw new RuntimeException(e);
        }
    }

    private boolean validateEventType(EventType expected, EventType actual, Acknowledgment acknowledgment) {
        if (!expected.equals(actual)) {
            log.warn("처리 불가능한 이벤트 타입: {}", actual);
            acknowledgment.acknowledge();
            return true;
        }
        return false;
    }
}