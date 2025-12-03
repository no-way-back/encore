package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import com.nowayback.payment.domain.payment.repository.PaymentStatusLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static com.nowayback.payment.fixture.PaymentStatusLogFixture.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("결제 상태 기록 리포지토리")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PaymentStatusLogRepositoryImpl.class)
@EnableJpaAuditing
@Testcontainers
class PaymentStatusLogRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private PaymentStatusLogRepository paymentStatusLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("결제 상태 기록 저장")
    class Save {

        @Test
        @DisplayName("저장에 성공한다.")
        void save_success() {
            /* given */
            PaymentStatusLog log = createPaymentStatusLog();

            /* when */
            PaymentStatusLog savedLog = paymentStatusLogRepository.save(log);

            /* then */
            assertThat(savedLog.getId()).isNotNull();
            assertThat(savedLog.getPaymentId()).isEqualTo(log.getPaymentId());
        }
    }

    @Nested
    @DisplayName("결제 ID로 결제 상태 기록 목록 조회")
    class FindAllByPaymentId {

        @Test
        @DisplayName("목록 조회에 성공한다.")
        void findAllByPaymentId_success() {
            /* given */
            PaymentStatusLog log1 = createPaymentStatusLog();
            PaymentStatusLog log2 = createPaymentStatusLog();

            entityManager.persist(log1);
            entityManager.persist(log2);

            /* when */
            Page<PaymentStatusLog> logs = paymentStatusLogRepository.findAllByPaymentId(PAYMENT_ID, PAGEABLE);

            /* then */
            assertThat(logs.getTotalElements()).isEqualTo(2);
            assertThat(Objects.requireNonNull(logs.getSort().getOrderFor("createdAt")).getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }
}