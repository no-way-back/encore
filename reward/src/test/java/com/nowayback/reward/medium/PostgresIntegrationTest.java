package com.nowayback.reward.medium;

import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 실제 PostgreSQL(Testcontainers)을 쓰는 통합 테스트의 공통 설정.
 * - 컨테이너는 한 번만 띄워 모든 통합 테스트 클래스가 함께 쓴다
 * - 외부 인프라(S3, Kafka 발행, Redis)는 mock으로 대체한다
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class PostgresIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    static {
        POSTGRES.start();
    }

    @MockitoBean
    protected S3Client s3Client;

    @MockitoBean
    protected KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    protected RedissonClient redissonClient;
}
