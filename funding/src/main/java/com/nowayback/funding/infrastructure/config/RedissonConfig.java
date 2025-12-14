package com.nowayback.funding.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.ssl.enabled:false}")
	private boolean redisSslEnabled;

	@Bean
	public RedissonClient redisson() {
		Config config = new Config();

		String protocol = redisSslEnabled ? "rediss://" : "redis://";

		config.useSingleServer()
			.setAddress(protocol + redisHost + ":" + redisPort)
			.setConnectionMinimumIdleSize(2)
			.setConnectionPoolSize(5)
			.setTimeout(3000)
			.setRetryAttempts(3);

		return Redisson.create(config);
	}
}
