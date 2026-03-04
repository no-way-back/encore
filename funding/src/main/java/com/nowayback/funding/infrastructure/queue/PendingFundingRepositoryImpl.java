package com.nowayback.funding.infrastructure.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nowayback.funding.domain.queue.vo.PendingFunding;
import com.nowayback.funding.domain.queue.repository.PendingFundingRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 PendingFunding Repository 구현
 */
@Repository
@Slf4j
public class PendingFundingRepositoryImpl implements PendingFundingRepository {

    private static final String PENDING_PREFIX = "pending:funding:";
    private static final long TTL_MINUTES = 30; // 30분 TTL

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public PendingFundingRepositoryImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void save(PendingFunding pendingFunding) {
        try {
            String key = PENDING_PREFIX + pendingFunding.userId();
            RBucket<String> bucket = redissonClient.getBucket(key);

            String json = serialize(pendingFunding);

            // 30분 TTL 설정
            bucket.set(json, TTL_MINUTES, TimeUnit.MINUTES);

            log.info("대기 펀딩 저장 완료 - userId: {}", pendingFunding.userId());

        } catch (Exception e) {
            log.error("대기 펀딩 저장 실패 - userId: {}", pendingFunding.userId(), e);
            throw new RuntimeException("대기 펀딩 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Optional<PendingFunding> findByUserId(UUID userId) {
        try {
            String key = PENDING_PREFIX + userId;
            RBucket<String> bucket = redissonClient.getBucket(key);

            String json = bucket.get();
            if (json == null) {
                return Optional.empty();
            }

            PendingFunding pendingFunding = deserialize(json);
            return Optional.of(pendingFunding);

        } catch (Exception e) {
            log.error("대기 펀딩 조회 실패 - userId: {}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            String key = PENDING_PREFIX + userId;
            redissonClient.getBucket(key).delete();

            log.debug("대기 펀딩 삭제 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("대기 펀딩 삭제 실패 - userId: {}", userId, e);
        }
    }

    // === Private Helper Methods ===

    private String serialize(PendingFunding pendingFunding) {
        try {
            return objectMapper.writeValueAsString(pendingFunding);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("PendingFunding 직렬화 실패", e);
        }
    }

    private PendingFunding deserialize(String json) {
        try {
            return objectMapper.readValue(json, PendingFunding.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("PendingFunding 역직렬화 실패", e);
        }
    }
}