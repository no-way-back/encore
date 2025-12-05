package com.nowayback.payment.infrastructure.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import com.nowayback.payment.domain.settlement.repository.SettlementStatusLogRepository;
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

import static com.nowayback.payment.fixture.SettlementStatusLogFixture.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("정산 상태 기록 리포지토리")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SettlementStatusLogRepositoryImpl.class)
@EnableJpaAuditing
@Testcontainers
class SettlementStatusLogRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private SettlementStatusLogRepository settlementStatusLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("정산 상태 기록 저장")
    class Save {

        @Test
        @DisplayName("저장에 성공한다.")
        void save_success() {
            /* given */
            SettlementStatusLog log = createSettlementStatusLog();

            /* when */
            SettlementStatusLog savedLog = settlementStatusLogRepository.save(log);

            /* then */
            assertThat(savedLog.getId()).isNotNull();
            assertThat(savedLog.getSettlementId()).isEqualTo(log.getSettlementId());
        }
    }

    @Nested
    @DisplayName("정산 ID로 정산 상태 기록 목록 조회")
    class FindAllBySettlementId {

        @Test
        @DisplayName("목록 조회에 성공한다.")
        void findAllBySettlementId_success() {
            /* given */
            SettlementStatusLog log1 = createSettlementStatusLog();
            SettlementStatusLog log2 = createSettlementStatusLog();

            entityManager.persist(log1);
            entityManager.persist(log2);
            entityManager.flush();

            /* when */
            Page<SettlementStatusLog> logs = settlementStatusLogRepository.findAllBySettlementId(SETTLEMENT_ID, PAGEABLE);

            /* then */
            assertThat(logs.getTotalElements()).isEqualTo(2);
            assertThat(Objects.requireNonNull(logs.getSort().getOrderFor("createdAt")).getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }
}