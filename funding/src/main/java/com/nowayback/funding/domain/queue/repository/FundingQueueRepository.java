package com.nowayback.funding.domain.queue.repository;

import com.nowayback.funding.domain.queue.vo.QueueEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 대기열 Repository 인터페이스
 * Redis 기반 구현을 위한 도메인 계층 인터페이스
 */
public interface FundingQueueRepository {

    /**
     * 대기열에 추가
     * @param entry 대기열 엔트리
     * @return 대기 순번 (1부터 시작)
     */
    Long enqueue(QueueEntry entry);

    /**
     * 대기열에서 다음 처리할 엔트리 조회 (여러 개)
     * @param count 조회할 개수
     * @return 대기열 엔트리 리스트
     */
    List<QueueEntry> dequeue(int count);

    /**
     * 사용자의 대기 순번 조회
     * @param userId 사용자 ID
     * @return 대기 순번 (1부터 시작, 없으면 empty)
     */
    Optional<Long> getPosition(UUID userId);

    /**
     * 사용자의 대기열 엔트리 조회
     * @param userId 사용자 ID
     * @return 대기열 엔트리
     */
    Optional<QueueEntry> getEntry(UUID userId);

    /**
     * 대기열에서 제거
     * @param userId 사용자 ID
     */
    void removeByUserId(UUID userId);

    /**
     * 전체 대기열 크기 조회
     * @return 대기 중인 총 인원
     */
    Long getQueueSize();

    /**
     * 만료된 엔트리 제거
     * @return 제거된 개수
     */
    Long removeExpired();
}