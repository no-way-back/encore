package com.nowayback.funding.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import org.redisson.api.RedissonClient;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
	"spring.jpa.properties.hibernate.default_schema=",
	"spring.sql.init.mode=never",  // SQL 스크립트 실행 안 함
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
	private UUID paymentId;

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
		paymentId = UUID.randomUUID();

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
		setupPaymentServiceStub_Success();

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
		assertThat(savedFunding.getAmount()).isEqualTo(45000L); // 40000 + 5000
		assertThat(savedFunding.getReservations()).hasSize(1);

		// Reward 호출 검증
		verify(1, postRequestedFor(urlEqualTo("/internal/rewards/reserve-stock")));

		// Payment 호출 검증 (URL 수정됨)
		verify(1, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	@Test
	@DisplayName("정상 플로우 - 리워드 여러 개")
	void createFunding_WithMultipleRewards_Success() {
		setupRewardServiceStub_MultipleRewards();
		setupPaymentServiceStub_Success();

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

		CreateFundingResult result = fundingService.createFunding(command);
		assertThat(result.status()).isEqualTo("SUCCESS");
	}

	@Test
	@DisplayName("Reward 서비스 실패 - 재고 부족")
	void createFunding_RewardOutOfStock_Failure() {
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

		assertThatThrownBy(() -> fundingService.createFunding(command))
			.isInstanceOf(Exception.class);

		// Payment 호출되지 않아야 함 (URL 수정됨)
		verify(0, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	@Test
	@DisplayName("Payment 서비스 실패 - 보상 트랜잭션 발생")
	void createFunding_PaymentFailed_CompensationTriggered() {
		setupRewardServiceStub_OneReward();

		// Payment 실패 Stub (URL 수정됨)
		stubFor(post(urlEqualTo("/payments/confirm"))
			.willReturn(aResponse()
				.withStatus(500)
				.withHeader("Content-Type", "application/json")
				.withBody("""
                    {
                        "code": "PAYMENT_FAILED",
                        "message": "결제 처리 실패"
                    }
                    """)));

		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			List.of(new CreateFundingCommand.RewardItem(rewardId1, optionId1, 2)),
			5000L,
			"payment_key",
			"order_id",
			"CARD",
			UUID.randomUUID().toString()
		);

		CreateFundingResult result = fundingService.createFunding(command);
		assertThat(result.status()).isEqualTo("FAILURE");

		// Reward는 호출됨
		verify(1, postRequestedFor(urlEqualTo("/internal/rewards/reserve-stock")));

		// Payment도 호출됨
		verify(1, postRequestedFor(urlEqualTo("/payments/confirm")));
	}

	@Test
	@DisplayName("요청 본문 검증 - Payment 서비스")
	void createFunding_VerifyPaymentRequestBody() {
		setupRewardServiceStub_OneReward();
		setupPaymentServiceStub_Success();

		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			List.of(new CreateFundingCommand.RewardItem(rewardId1, optionId1, 2)),
			5000L,
			"test_payment_key",
			"test_order_id",
			"CARD",
			UUID.randomUUID().toString()
		);

		fundingService.createFunding(command);

		// JSONPath 검증 (URL 수정됨)
		verify(postRequestedFor(urlEqualTo("/payments/confirm"))
			.withRequestBody(matchingJsonPath("$.fundingId"))
			.withRequestBody(matchingJsonPath("$.paymentKey", equalTo("test_payment_key")))
			.withRequestBody(matchingJsonPath("$.orderId", equalTo("test_order_id")))
			.withRequestBody(matchingJsonPath("$.paymentMethod", equalTo("CARD"))));
	}

	// ==================== Helper Methods ====================

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

	private void setupPaymentServiceStub_Success() {
		stubFor(post(urlEqualTo("/payments/confirm"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody(String.format("""
                    {
                        "paymentId": "%s"
                    }
                    """, paymentId))));
	}
}
