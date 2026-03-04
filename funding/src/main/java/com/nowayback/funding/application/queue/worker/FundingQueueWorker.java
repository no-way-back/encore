package com.nowayback.funding.application.queue.worker;

import com.nowayback.funding.application.client.reward.RewardClient;
import com.nowayback.funding.application.client.reward.dto.request.StockReserveRequest;
import com.nowayback.funding.application.client.reward.dto.response.StockReserveResponse;
import com.nowayback.funding.application.funding.dto.event.FundingPaymentProcessEvent;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.queue.vo.PendingFunding;
import com.nowayback.funding.domain.queue.vo.QueueEntry;
import com.nowayback.funding.domain.queue.repository.FundingQueueRepository;
import com.nowayback.funding.domain.queue.repository.PendingFundingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 대기열 백그라운드 워커
 *
 * 주기적으로 대기열에서 사용자를 꺼내서 펀딩 처리
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FundingQueueWorker {

    private final FundingQueueRepository queueRepository;
    private final PendingFundingRepository pendingFundingRepository;
    private final FundingRepository fundingRepository;
    private final RewardClient rewardClient;
    private final OutboxService outboxService;

    @Value("${queue.processing.rate:10}")
    private int processingRate; // 초당 처리 개수

    /**
     * 대기열 처리
     *
     * 1초마다 실행
     * - 대기열에서 N명 꺼내기 (dequeue)
     * - 각 사용자별로 펀딩 처리
     * - 성공/실패 로깅
     */
    @Scheduled(fixedRate = 1000) // 1초마다
    public void processQueue() {
        try {
            // 1. 대기열에서 꺼내기
            List<QueueEntry> entries = queueRepository.dequeue(processingRate);

            if (entries.isEmpty()) {
                return; // 처리할 대기열 없음
            }

            log.info("대기열 처리 시작 - {} 건", entries.size());

            // 2. 각각 처리
            int successCount = 0;
            int failCount = 0;

            for (QueueEntry entry : entries) {
                try {
                    processFunding(entry);
                    successCount++;
                } catch (Exception e) {
                    log.error("펀딩 처리 실패 - userId: {}", entry.userId(), e);
                    failCount++;

                    // 실패 시 대기 데이터 삭제
                    pendingFundingRepository.deleteByUserId(entry.userId());
                }
            }

            log.info("대기열 처리 완료 - 성공: {}, 실패: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("대기열 처리 중 오류 발생", e);
        }
    }

    /**
     * 개별 펀딩 처리
     *
     * @param entry 대기열 엔트리
     */
    @Transactional
    protected void processFunding(QueueEntry entry) {
        log.info("펀딩 처리 시작 - userId: {}, projectId: {}",
                entry.userId(), entry.projectId());

        // 1. 대기 펀딩 데이터 조회
        Optional<PendingFunding> pendingOpt = pendingFundingRepository.findByUserId(entry.userId());
        if (pendingOpt.isEmpty()) {
            log.warn("대기 펀딩 데이터 없음 - userId: {}", entry.userId());
            return;
        }

        PendingFunding pending = pendingOpt.get();

        // 2. Funding 엔티티 생성
        Funding funding = Funding.createFunding(
                pending.projectId(),
                pending.userId(),
                pending.idempotencyKey(),
                pending.amount()
        );

        Funding savedFunding = fundingRepository.save(funding);
        log.info("Funding 생성 완료 - fundingId: {}", savedFunding.getId());

        // 3. 리워드 재고 예약 (있으면)
        if (pending.hasRewards()) {
            try {
                StockReserveResponse response = reserveStock(pending, savedFunding);

                log.info("재고 예약 완료 - fundingId: {}, reservations: {}",
                        savedFunding.getId(), response.reservedItems().size());

                // 예약 정보 추가
                addReservationsToFunding(savedFunding, response);
                savedFunding.updateAmount(response.totalAmount() + pending.amount());

                // 재고 확정
                rewardClient.confirmReservations(pending.userId(), savedFunding.getId());

            } catch (Exception e) {
                log.error("재고 예약 실패 - fundingId: {}", savedFunding.getId(), e);
                throw new RuntimeException("재고 예약 실패", e);
            }
        }

        // 4. 결제 이벤트 발행
        publishPaymentProcessEvent(savedFunding, pending);

        // 5. 대기 펀딩 데이터 삭제
        pendingFundingRepository.deleteByUserId(entry.userId());

        log.info("펀딩 처리 완료 - fundingId: {}", savedFunding.getId());
    }

    /**
     * 재고 예약
     */
    private StockReserveResponse reserveStock(PendingFunding pending, Funding funding) {
        List<StockReserveRequest.StockReserveItem> items = pending.rewardItems().stream()
                .map(item -> new StockReserveRequest.StockReserveItem(
                        item.rewardId(),
                        item.optionId(),
                        item.quantity()
                ))
                .toList();

        StockReserveRequest request = new StockReserveRequest(funding.getId(), items);
        return rewardClient.reserveStock(pending.userId(), request);
    }

    /**
     * 예약 정보 추가
     */
    private void addReservationsToFunding(Funding funding, StockReserveResponse response) {
        for (StockReserveResponse.ReservedItem item : response.reservedItems()) {
            funding.addReservation(
                    item.reservationId(),
                    item.rewardId(),
                    item.optionId(),
                    item.quantity(),
                    item.itemAmount()
            );
        }
    }

    /**
     * 결제 이벤트 발행
     */
    private void publishPaymentProcessEvent(Funding funding, PendingFunding pending) {
        FundingPaymentProcessEvent event = FundingPaymentProcessEvent.of(
                funding.getId(),
                pending.projectId(),
                pending.userId(),
                funding.getAmount()
        );

        outboxService.publish(event);
    }

    /**
     * 만료된 엔트리 정리
     *
     * 5분마다 실행
     */
    @Scheduled(fixedRate = 300000) // 5분
    public void cleanupExpired() {
        try {
            Long removed = queueRepository.removeExpired();

            if (removed > 0) {
                log.info("만료된 대기열 정리 완료 - {} 건", removed);
            }

        } catch (Exception e) {
            log.error("만료된 대기열 정리 실패", e);
        }
    }
}