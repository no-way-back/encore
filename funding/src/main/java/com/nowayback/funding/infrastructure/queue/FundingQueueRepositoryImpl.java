package com.nowayback.funding.infrastructure.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nowayback.funding.domain.queue.vo.QueueEntry;
import com.nowayback.funding.domain.queue.repository.FundingQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.client.protocol.ScoredEntry;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Redis 기반 대기열 Repository 구현
 * Redisson의 Sorted Set을 사용하여 순서 보장
 *
 * 수정 사항:
 * 1. dequeue 실제 제거 구현
 * 2. getPosition O(1) 최적화
 * 3. 중복 등록 방지
 * 4. removeExpired entry 정리 추가
 * 5. userId를 직접 키로 사용
 */
@Repository
@Slf4j
public class FundingQueueRepositoryImpl implements FundingQueueRepository {

    private static final String QUEUE_KEY = "funding:queue";
    private static final String ENTRY_PREFIX = "funding:queue:entry:";

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public FundingQueueRepositoryImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Long enqueue(QueueEntry entry) {
        try {
            RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(QUEUE_KEY);

            // ⭐ userId를 키로 사용 (중복 방지)
            String userId = entry.userId().toString();
            boolean added = queue.add(entry.enqueuedAt(), userId);

            if (!added) {
                // 이미 대기열에 존재
                log.warn("이미 대기열에 존재 - userId: {}", entry.userId());
                Integer rank = queue.rank(userId);
                return rank != null ? rank.longValue() + 1L : 1L;
            }

            // 엔트리 상세 정보 저장
            String entryKey = ENTRY_PREFIX + entry.userId();
            redissonClient.getBucket(entryKey).set(serializeEntry(entry));

            // 순번 반환
            Integer rank = queue.rank(userId);
            long position = rank != null ? rank.longValue() + 1L : 1L;

            log.info("대기열 등록 완료 - userId: {}, position: {}", entry.userId(), position);

            return position;

        } catch (Exception e) {
            log.error("대기열 등록 실패 - userId: {}", entry.userId(), e);
            throw new RuntimeException("대기열 등록 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<QueueEntry> dequeue(int count) {
        try {
            RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(QUEUE_KEY);

            // ⭐ 배치로 원자적 Pop (ZPOPMIN count)
            Collection<ScoredEntry<String>> popped = queue.pollFirstEntries(count);

            if (popped.isEmpty()) {
                return Collections.emptyList();
            }

            List<QueueEntry> entries = new ArrayList<>();

            for (ScoredEntry<String> scoredEntry : popped) {
                String userId = scoredEntry.getValue();

                // 엔트리 조회
                Optional<QueueEntry> entryOpt = getEntry(UUID.fromString(userId));

                if (entryOpt.isPresent()) {
                    entries.add(entryOpt.get());
                }

                // ⭐ 엔트리 삭제 (메모리 정리)
                String entryKey = ENTRY_PREFIX + userId;
                redissonClient.getBucket(entryKey).delete();
            }

            log.info("대기열에서 {} 건 조회 및 제거 완료", entries.size());

            return entries;

        } catch (Exception e) {
            log.error("대기열 조회 실패", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Long> getPosition(UUID userId) {
        try {
            RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(QUEUE_KEY);

            // ⭐ O(log N) - 전체 조회 안 함!
            String userIdStr = userId.toString();
            Integer rank = queue.rank(userIdStr);

            if (rank == null) {
                return Optional.empty();
            }

            return Optional.of(rank.longValue() + 1L);

        } catch (Exception e) {
            log.error("순번 조회 실패 - userId: {}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<QueueEntry> getEntry(UUID userId) {
        try {
            String entryKey = ENTRY_PREFIX + userId;
            String json = (String) redissonClient.getBucket(entryKey).get();

            if (json == null) {
                return Optional.empty();
            }

            QueueEntry entry = deserializeEntry(json);

            // ⭐ 만료 체크
            if (entry.isExpired()) {
                log.info("만료된 엔트리 - userId: {}", userId);
                removeByUserId(entry.userId());
                return Optional.empty();
            }

            return Optional.of(entry);

        } catch (Exception e) {
            log.error("엔트리 조회 실패 - userId: {}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public void removeByUserId(UUID userId) {
        try {
            RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(QUEUE_KEY);

            // 1. Sorted Set에서 제거
            String userIdStr = userId.toString();
            queue.remove(userIdStr);

            // 2. 엔트리 제거
            String entryKey = ENTRY_PREFIX + userId;
            redissonClient.getBucket(entryKey).delete();

            log.debug("대기열 제거 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("대기열 제거 실패 - userId: {}", userId, e);
        }
    }

    @Override
    public Long getQueueSize() {
        try {
            RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(QUEUE_KEY);
            return (long) queue.size();
        } catch (Exception e) {
            log.error("대기열 크기 조회 실패", e);
            return 0L;
        }
    }

    @Override
    public Long removeExpired() {
        try {
            RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(QUEUE_KEY);

            // 1. 만료된 userId 조회 (30분 이전)
            long expiredTime = System.currentTimeMillis() - (30 * 60 * 1000);
            Collection<String> expiredUserIds = queue.valueRange(0, true, expiredTime, true);

            if (expiredUserIds.isEmpty()) {
                return 0L;
            }

            // 2. Sorted Set에서 제거
            long removed = queue.removeRangeByScore(0, true, expiredTime, true);

            // 3. ⭐ 각 엔트리도 제거 (메모리 누수 방지)
            for (String userId : expiredUserIds) {
                String entryKey = ENTRY_PREFIX + userId;
                redissonClient.getBucket(entryKey).delete();
            }

            log.info("만료된 대기열 {} 건 제거 완료 (entry 포함)", removed);

            return removed;

        } catch (Exception e) {
            log.error("만료된 대기열 제거 실패", e);
            return 0L;
        }
    }

    // === Private Helper Methods ===

    private String serializeEntry(QueueEntry entry) {
        try {
            return objectMapper.writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("QueueEntry 직렬화 실패", e);
        }
    }

    private QueueEntry deserializeEntry(String json) {
        try {
            return objectMapper.readValue(json, QueueEntry.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("QueueEntry 역직렬화 실패", e);
        }
    }
}