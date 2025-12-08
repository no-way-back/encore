package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisTokenBlacklistRepository implements TokenBlacklistRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    @Override
    public void addToBlacklist(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;

        long ttl = expirationTime - System.currentTimeMillis();

        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofMillis(ttl));
            log.info("[Token Blacklist] 블랙 리스트에 토큰 추가: token={}..., ttl={}ms", token.substring(0, 20), ttl);
        } else {
            log.warn("[Token Blacklist] 만료된 토큰은 블랙 리스트에 추가할 수 없습니다: token={}", token);
        }
    }
}
