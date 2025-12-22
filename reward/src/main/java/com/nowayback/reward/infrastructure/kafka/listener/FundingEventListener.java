package com.nowayback.reward.infrastructure.kafka.listener;

import com.nowayback.reward.application.inbox.InboxProcessor;
import com.nowayback.reward.application.qrcode.QRCodeService;
import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.ProjectFundingSuccessEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingCompletedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingFailedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingRefundEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.nowayback.reward.domain.outbox.vo.EventType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundingEventListener {

    private final RewardStockService rewardStockService;
    private final QRCodeService qrCodeService;
    private final InboxProcessor inboxProcessor;

    /**
     * 펀딩 결제 실패 이벤트 처리
     * - 펀딩에 포함된 모든 리워드의 예약 재고를 복원한다
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.funding-failed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "fundingFailedListenerFactory"
    )
    public void consumeFundingFailedEvent(
            @Payload FundingFailedEvent event,
            Acknowledgment acknowledgment
    ) {
        log.info("결제 실패 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                event.eventId(),
                event.eventType(),
                event.payload().fundingId()
        );

        if (validateEventType(FUNDING_FAILED, event.eventType(), acknowledgment)) {
            return;
        }

        try {
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
            log.error("결제 실패 이벤트 처리 실패 - 펀딩: {}, 에러 핸들러가 재시도 처리",
                    event.payload().fundingId(), e);
            throw e;
        }
    }

    /**
     * 펀딩 환불 이벤트 처리
     * - 펀딩에 포함된 모든 리워드의 예약 재고를 복원한다
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.funding-refund}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "fundingRefundListenerFactory"
    )
    public void consumeFundingRefundEvent(
            @Payload FundingRefundEvent event,
            Acknowledgment acknowledgment
    ) {
        log.info("펀딩 환불 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                event.eventId(),
                event.eventType(),
                event.payload().fundingId()
        );

        if (validateEventType(FUNDING_REFUND, event.eventType(), acknowledgment)) {
            return;
        }

        try {
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
            log.error("펀딩 환불 이벤트 처리 실패 - 펀딩: {}, 에러 핸들러가 재시도 처리",
                    event.payload().fundingId(), e);
            throw e;
        }
    }

    /**
     * 펀딩 완료 이벤트 처리
     * - 펀딩 완료 시 QR 코드를 생성한다
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.funding-completed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "fundingCompletedListenerFactory"
    )
    public void consumeFundingCompletedEvent(
            @Payload FundingCompletedEvent event,
            Acknowledgment acknowledgment
    ) {
        log.info("펀딩 완료 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                event.eventId(),
                event.eventType(),
                event.payload().fundingId()
        );

        if (validateEventType(FUNDING_COMPLETED, event.eventType(), acknowledgment)) {
            return;
        }

        try {
            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().fundingId(),
                    event.payload(),
                    payload -> qrCodeService.createQRCode(payload.toCommand())
            );

            acknowledgment.acknowledge();

            log.info("QR 코드 생성 완료 - 펀딩: {}", event.payload().fundingId());

        } catch (Exception e) {
            log.error("펀딩 완료 이벤트 처리 실패 - 펀딩: {}, 에러 핸들러가 재시도 처리",
                    event.payload().fundingId(), e);
            throw e;
        }
    }

    /**
     * 프로젝트 펀딩 성공 이벤트 처리
     * - 프로젝트의 모든 펀딩에 대한 QR 코드 이메일을 발송한다
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.project-funding-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "projectFundingSuccessListenerFactory"
    )
    public void consumeProjectFundingSuccessEvent(
            @Payload ProjectFundingSuccessEvent event,
            Acknowledgment acknowledgment
    ) {
        log.info("프로젝트 펀딩 성공 이벤트 수신 - ID: {}, 타입: {}, 펀딩: {}",
                event.eventId(),
                event.eventType(),
                event.payload().fundingId()
        );

        if (validateEventType(PROJECT_FUNDING_SUCCESS, event.eventType(), acknowledgment)) {
            return;
        }

        try {
            inboxProcessor.processEvent(
                    event.eventId(),
                    event.eventType(),
                    event.payload().fundingId(),
                    event.payload(),
                    payload -> qrCodeService.sendQRCodesByFunding(payload.fundingId())
            );

            acknowledgment.acknowledge();

            log.info("QR 코드 이메일 발송 완료 - 펀딩: {}", event.payload().fundingId());

        } catch (Exception e) {
            log.error("프로젝트 펀딩 성공 이벤트 처리 실패 - 펀딩: {}, 에러 핸들러가 재시도 처리",
                    event.payload().fundingId(), e);
            throw e;
        }
    }

    /**
     * 처리 가능한 이벤트인지 확인
     */
    private boolean validateEventType(EventType eventType, EventType event, Acknowledgment acknowledgment) {
        if (!eventType.equals(event)) {
            log.warn("처리 불가능한 이벤트 타입: {}", event);
            acknowledgment.acknowledge();
            return true;
        }

        return false;
    }
}