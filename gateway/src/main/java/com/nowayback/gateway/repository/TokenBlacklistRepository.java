package com.nowayback.gateway.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklistRepository {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    public Mono<Boolean> isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;

        return redisTemplate.hasKey(key)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("[Gateway Filter] 블랙리스트 토큰 감지: {}...", token.substring(0, 20));
                    }
                })
                .defaultIfEmpty(false)
                .onErrorResume(e -> {
                    log.error("[Gateway Filter] Redis 조회 실패", e);
                    return Mono.just(false);
                });
    }
}
