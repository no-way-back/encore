package com.nowayback.reward.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean redisSslEnabled;

    @Value("${redisson.connection-pool-size:50}")
    private int connectionPoolSize;

    @Value("${redisson.connection-minimum-idle-size:10}")
    private int connectionMinimumIdleSize;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        String protocol = redisSslEnabled ? "rediss://" : "redis://";

        config.useSingleServer()
                .setAddress(protocol + redisHost + ":" + redisPort)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setTimeout(3000)
                .setConnectTimeout(3000)
                .setIdleConnectionTimeout(10000)
                .setRetryAttempts(3);

        RedissonClient redisson = Redisson.create(config);

        log.info("Redisson 클라이언트 초기화 완료 - {}://{}:{} (SSL: {}, PoolSize: {}, MinIdle: {})",
                protocol.replace("://", ""), redisHost, redisPort,
                redisSslEnabled, connectionPoolSize, connectionMinimumIdleSize);

        return redisson;
    }
}