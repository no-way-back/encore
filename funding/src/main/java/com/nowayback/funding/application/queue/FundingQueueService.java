package com.nowayback.funding.application.queue;

import com.nowayback.funding.application.queue.dto.command.EnqueueFundingCommand;
import com.nowayback.funding.application.queue.dto.result.EnqueueResult;
import com.nowayback.funding.application.queue.dto.result.QueuePositionResult;
import com.nowayback.funding.domain.queue.vo.QueueEntry;
import com.nowayback.funding.domain.queue.repository.FundingQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * 펀딩 대기열 서비스 구현
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FundingQueueService  {

    private final FundingQueueRepository queueRepository;

    @Value("${queue.processing.rate:10}")
    private int processingRate; // 초당 처리 개수 (기본값: 10)

    public EnqueueResult enqueue(EnqueueFundingCommand command) {
        log.info("대기열 등록 시작 - userId: {}, projectId: {}", command.userId(), command.projectId());

        // 1. 이미 대기열에 있는지 확인
        Optional<QueueEntry> existingEntry = queueRepository.getEntry(command.userId());
        if (existingEntry.isPresent()) {
            Long position = queueRepository.getPosition(command.userId()).orElse(0L);
            Long estimatedWaitTime = calculateEstimatedWaitTime(position);

            log.warn("이미 대기열에 존재 - userId: {}, position: {}", command.userId(), position);

            // ✅ userId를 queueId로 사용
            return EnqueueResult.of(command.userId().toString(), position, estimatedWaitTime);
        }

        // 2. 새로운 대기열 엔트리 생성
        QueueEntry entry = QueueEntry.create(command.userId(), command.projectId());

        // 3. 대기열에 추가
        Long position = queueRepository.enqueue(entry);

        // 4. 예상 대기 시간 계산
        Long estimatedWaitTime = calculateEstimatedWaitTime(position);

        log.info("대기열 등록 완료 - userId: {}, position: {}, estimatedWaitTime: {}초",
                command.userId(), position, estimatedWaitTime);

        // ✅ userId를 queueId로 사용
        return EnqueueResult.of(entry.userId().toString(), position, estimatedWaitTime);
    }

    public QueuePositionResult getPosition(UUID userId) {
        log.debug("순번 조회 - userId: {}", userId);

        // 1. 대기열에서 순번 조회
        Optional<Long> positionOpt = queueRepository.getPosition(userId);

        if (positionOpt.isEmpty()) {
            log.debug("대기열에 없음 - userId: {}", userId);
            return QueuePositionResult.notFound();
        }

        Long position = positionOpt.get();

        // 2. 처리 중인지 확인 (1번이면 처리 중)
        if (position == 1L) {
            log.debug("처리 중 - userId: {}", userId);
            return QueuePositionResult.processing();
        }

        // 3. 대기 중
        Long totalWaiting = queueRepository.getQueueSize();
        Long estimatedWaitTime = calculateEstimatedWaitTime(position);

        log.debug("대기 중 - userId: {}, position: {}, estimatedWaitTime: {}초",
                userId, position, estimatedWaitTime);

        return QueuePositionResult.waiting(position, totalWaiting, estimatedWaitTime);
    }

    public void remove(UUID userId) {
        log.info("대기열 제거 - userId: {}", userId);

        Optional<QueueEntry> entryOpt = queueRepository.getEntry(userId);
        if (entryOpt.isEmpty()) {
            log.warn("대기열에 없는 사용자 제거 시도 - userId: {}", userId);
            return;
        }

        queueRepository.removeByUserId(userId);

        log.info("대기열 제거 완료 - userId: {}", userId);
    }

    /**
     * 예상 대기 시간 계산
     * @param position 현재 순번
     * @return 예상 대기 시간 (초)
     */
    private Long calculateEstimatedWaitTime(Long position) {
        if (position == null || position <= 0) {
            return 0L;
        }

        // 예상 대기 시간 = (내 앞에 있는 사람 수) / (초당 처리 개수)
        long waitingAhead = position - 1; // 내 앞에 대기 중인 사람
        return waitingAhead / processingRate;
    }
}