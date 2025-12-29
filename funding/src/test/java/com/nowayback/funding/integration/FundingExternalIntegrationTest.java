package com.nowayback.funding.integration;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.default_schema=",
		"spring.sql.init.mode=never",
		"spring.jpa.defer-datasource-initialization=false",
		"spring.data.redis.host=localhost",
		"spring.data.redis.port=6379",
		"feign.client.config.reward-service.url=http://localhost:${wiremock.server.port}",
		"feign.client.config.payment-service.url=http://localhost:${wiremock.server.port}"
})
@Transactional
@DisplayName("Funding 외부 연동 통합 테스트 (WireMock)")
class FundingExternalIntegrationTest {

	@MockitoBean
	private RedissonClient redissonClient;

	@Autowired
	private FundingService fundingService;

	@Autowired
	private FundingRepository fundingRepository;

	@Autowired
	private FundingProjectStatisticsRepository fundingProjectStatisticsRepository;

	private UUID projectId;
	private UUID userId;
	private UUID fundingId;
	private UUID rewardId1;
	private UUID rewardId2;
	private UUID optionId1;
	private UUID reservationId1;
	private UUID reservationId2;

	@BeforeEach
	void setUp() {
		projectId = UUID.randomUUID();
		userId = UUID.randomUUID();
		fundingId = UUID.randomUUID();
		rewardId1 = UUID.randomUUID();
		rewardId2 = UUID.randomUUID();
		optionId1 = UUID.randomUUID();
		reservationId1 = UUID.randomUUID();
		reservationId2 = UUID.randomUUID();

		FundingProjectStatistics stats = FundingProjectStatistics.create(
				projectId,
				UUID.randomUUID(),
				1_000_000L,
				LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(30)
		);

		fundingProjectStatisticsRepository.save(stats);

		reset();
	}

	@Test
	@DisplayName("정상 플로우 - 리워드 1개 + HTTP 실제 호출")
	void createFunding_WithOneReward_RealHttpCall_Success() {
		// given
		setupRewardServiceStub_OneReward();

		CreateFundingCommand.RewardItem rewardItem =
				new CreateFundingCommand.RewardItem(rewardId1, optionId1, 2);

		CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(rewardItem),
				5000L,
				"payment_key",
				"order_id",
				"CARD",
				UUID.randomUUID().toString()
		);

		// when
		CreateFundingResult result = fundingService.createFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");

		Funding savedFunding = fundingRepository.findById(result.fundingId()).orElseThrow();
		assertThat(savedFunding.getAmount()).isEqualTo(45000L);
		assertThat(savedFunding.getStatus()).isEqualTo(FundingStatus.PENDING);
		assertThat(savedFunding.getReservations()).hasSize(1);

		verify(1, postRequestedFor(urlEqualTo("/internal/rewards/reserve-stock")));
		verify(0, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	@Test
	@DisplayName("정상 플로우 - 리워드 여러 개")
	void createFunding_WithMultipleRewards_Success() {
		// given
		setupRewardServiceStub_MultipleRewards();

		CreateFundingCommand.RewardItem item1 =
				new CreateFundingCommand.RewardItem(rewardId1, optionId1, 2);
		CreateFundingCommand.RewardItem item2 =
				new CreateFundingCommand.RewardItem(rewardId2, null, 1);

		CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(item1, item2),
				5000L,
				"payment_key",
				"order_id",
				"CARD",
				UUID.randomUUID().toString()
		);

		// when
		CreateFundingResult result = fundingService.createFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");

		Funding savedFunding = fundingRepository.findById(result.fundingId()).orElseThrow();
		assertThat(savedFunding.getAmount()).isEqualTo(75000L);
		assertThat(savedFunding.getStatus()).isEqualTo(FundingStatus.PENDING);
		assertThat(savedFunding.getReservations()).hasSize(2);

		verify(1, postRequestedFor(urlEqualTo("/internal/rewards/reserve-stock")));
		verify(0, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	@Test
	@DisplayName("정상 플로우 - 순수 후원 (리워드 없음)")
	void createFunding_PureDonation_Success() {
		// given
		CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(),
				10000L,
				"payment_key",
				"order_id",
				"CARD",
				UUID.randomUUID().toString()
		);

		// when
		CreateFundingResult result = fundingService.createFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");

		Funding savedFunding = fundingRepository.findById(result.fundingId()).orElseThrow();
		assertThat(savedFunding.getAmount()).isEqualTo(10000L);
		assertThat(savedFunding.getStatus()).isEqualTo(FundingStatus.PENDING);
		assertThat(savedFunding.getReservations()).isEmpty();

		verify(0, postRequestedFor(urlEqualTo("/internal/rewards/reserve-stock")));
		verify(0, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	@Test
	@DisplayName("Reward 서비스 실패 - 재고 부족")
	void createFunding_RewardOutOfStock_Failure() {
		// given
		stubFor(post(urlEqualTo("/internal/rewards/reserve-stock"))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "application/json")
						.withBody("""
                    {
                        "code": "OUT_OF_STOCK",
                        "message": "재고가 부족합니다."
                    }
                    """)));

		CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(new CreateFundingCommand.RewardItem(rewardId1, optionId1, 999)),
				5000L,
				"payment_key",
				"order_id",
				"CARD",
				UUID.randomUUID().toString()
		);

		// when & then
		assertThatThrownBy(() -> fundingService.createFunding(command))
				.isInstanceOf(Exception.class);

		verify(0, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	private void setupRewardServiceStub_OneReward() {
		stubFor(post(urlEqualTo("/internal/rewards/reserve-stock"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(String.format("""
                    {
                        "fundingId": "%s",
                        "reservedItems": [
                            {
                                "reservationId": "%s",
                                "rewardId": "%s",
                                "optionId": "%s",
                                "quantity": 2,
                                "itemAmount": 40000
                            }
                        ],
                        "totalAmount": 40000
                    }
                    """, fundingId, reservationId1, rewardId1, optionId1))));
	}

	private void setupRewardServiceStub_MultipleRewards() {
		stubFor(post(urlEqualTo("/internal/rewards/reserve-stock"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(String.format("""
                    {
                        "fundingId": "%s",
                        "reservedItems": [
                            {
                                "reservationId": "%s",
                                "rewardId": "%s",
                                "optionId": "%s",
                                "quantity": 2,
                                "itemAmount": 40000
                            },
                            {
                                "reservationId": "%s",
                                "rewardId": "%s",
                                "optionId": null,
                                "quantity": 1,
                                "itemAmount": 30000
                            }
                        ],
                        "totalAmount": 70000
                    }
                    """, fundingId, reservationId1, rewardId1, optionId1,
								reservationId2, rewardId2))));
	}
}