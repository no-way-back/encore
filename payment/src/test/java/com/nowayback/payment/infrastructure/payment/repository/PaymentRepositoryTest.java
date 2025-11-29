package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static com.nowayback.payment.fixture.PaymentFixture.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("결제 리포지토리")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PaymentRepositoryImpl.class)
@EnableJpaAuditing
@Testcontainers
class PaymentRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("결제 저장")
    class Save {

        @Test
        @DisplayName("결제 저장에 성공한다.")
        void save_success() {
            /* given */
            Payment payment = createPayment();

            /* when */
            Payment savedPayment = paymentRepository.save(payment);

            /* then */
            assertThat(savedPayment.getId()).isNotNull();
            assertThat(savedPayment.getFundingId()).isEqualTo(payment.getFundingId());
        }
    }

    @Nested
    @DisplayName("펀딩 ID로 결제 조회")
    class FindByFundingId {

        @Test
        @DisplayName("펀딩 ID에 대한 결제가 존재하면 Optional<Payment>를 반환한다.")
        void findByFundingId_whenExists_returnPayment() {
            /* given */
            Payment payment = createPayment();
            entityManager.persist(payment);
            entityManager.flush();

            /* when */
            Optional<Payment> foundPayment = paymentRepository.findByFundingId(FUNDING_ID);

            /* then */
            assertThat(foundPayment).isPresent();
        }

        @Test
        @DisplayName("펀딩 ID에 대한 결제가 존재하지 않으면 빈 Optional을 반환한다.")
        void findByFundingId_whenNotExists_returnEmptyOptional() {
            /* given */
            /* when */
            Optional<Payment> foundPayment = paymentRepository.findByFundingId(FUNDING_ID);

            /* then */
            assertThat(foundPayment).isNotPresent();
        }
    }
}