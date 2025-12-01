package com.nowayback.payment.infrastructure.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.repository.SettlementRepository;
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

import static com.nowayback.payment.fixture.SettlementFixture.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("정산 리포지토리")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SettlementRepositoryImpl.class)
@EnableJpaAuditing
@Testcontainers
class SettlementRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("정산 저장")
    class Save {

        @Test
        @DisplayName("정산 저장에 성공한다.")
        void save_success() {
            /* given */
            Settlement settlement = createSettlement();

            /* when */
            Settlement savedSettlement = settlementRepository.save(settlement);

            /* then */
            assertThat(savedSettlement.getId()).isNotNull();
            assertThat(savedSettlement.getProjectId()).isEqualTo(settlement.getProjectId());
        }
    }

    @Nested
    @DisplayName("프로젝트 ID로 정산 조회")
    class FindByProjectId {

        @Test
        @DisplayName("프로젝트 ID에 대한 정산이 존재하면 정산을 반환한다.")
        void findByProjectId_whenExists_returnSettlement() {
            /* given */
            Settlement settlement = createSettlement();
            entityManager.persistAndFlush(settlement);

            /* when */
            Optional<Settlement> foundSettlement = settlementRepository.findByProjectId(PROJECT_ID);

            /* then */
            assertThat(foundSettlement).isPresent();
            assertThat(foundSettlement.get().getId()).isEqualTo(settlement.getId());
        }

        @Test
        @DisplayName("프로젝트 ID에 대한 정산이 존재하지 않으면 빈 Optional을 반환한다.")
        void findByProjectId_whenNotExists_returnEmptyOptional() {
            /* given */
            /* when */
            Optional<Settlement> foundSettlement = settlementRepository.findByProjectId(PROJECT_ID);

            /* then */
            assertThat(foundSettlement).isNotPresent();
        }
    }
}