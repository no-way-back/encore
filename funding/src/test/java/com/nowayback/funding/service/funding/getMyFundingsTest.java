package com.nowayback.funding.service.funding;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;
import com.nowayback.funding.application.funding.service.FundingServiceImpl;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FundingService - 내 후원 내역 조회 테스트")
class getMyFundingsTest {

	@InjectMocks
	private FundingServiceImpl fundingService;

	@Mock
	private FundingRepository fundingRepository;

	private UUID userId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
	}

	@Test
	@DisplayName("내 후원 내역 조회 성공")
	void getMyFundings_Success() {
		// given
		GetMyFundingsCommand command = new GetMyFundingsCommand(
			userId,
			null,
			GetMyFundingsCommand.FundingPeriod.ALL,
			GetMyFundingsCommand.FundingSortBy.LATEST,
			0,
			20
		);

		Funding funding1 = createFunding(15000L);
		Funding funding2 = createFunding(20000L);

		Page<Funding> fundingPage = new PageImpl<>(
			Arrays.asList(funding1, funding2),
			Pageable.ofSize(20),
			2L
		);

		given(fundingRepository.findMyFundings(any(), any(), any(), any()))
			.willReturn(fundingPage);

		// when
		GetMyFundingsResult result = fundingService.getMyFundings(command);

		// then
		assertThat(result.fundings()).hasSize(2);
		assertThat(result.pageInfo().totalElements()).isEqualTo(2L);
	}

	@Test
	@DisplayName("상태 필터링 성공")
	void getMyFundings_WithStatusFilter_Success() {
		// given
		GetMyFundingsCommand command = new GetMyFundingsCommand(
			userId,
			FundingStatus.COMPLETED,
			GetMyFundingsCommand.FundingPeriod.ALL,
			GetMyFundingsCommand.FundingSortBy.LATEST,
			0,
			20
		);

		Funding funding = createFunding(15000L);

		Page<Funding> fundingPage = new PageImpl<>(
			Arrays.asList(funding),
			Pageable.ofSize(20),
			1L
		);

		given(fundingRepository.findMyFundings(any(), eq(FundingStatus.COMPLETED), any(), any()))
			.willReturn(fundingPage);

		// when
		GetMyFundingsResult result = fundingService.getMyFundings(command);

		// then
		assertThat(result.fundings()).hasSize(1);
		assertThat(result.fundings().get(0).status()).isEqualTo("COMPLETED");
	}

	@Test
	@DisplayName("빈 결과 조회 성공")
	void getMyFundings_EmptyResult_Success() {
		// given
		GetMyFundingsCommand command = new GetMyFundingsCommand(
			userId,
			null,
			GetMyFundingsCommand.FundingPeriod.ALL,
			GetMyFundingsCommand.FundingSortBy.LATEST,
			0,
			20
		);

		Page<Funding> emptyPage = new PageImpl<>(
			Arrays.asList(),
			Pageable.ofSize(20),
			0L
		);

		given(fundingRepository.findMyFundings(any(), any(), any(), any()))
			.willReturn(emptyPage);

		// when
		GetMyFundingsResult result = fundingService.getMyFundings(command);

		// then
		assertThat(result.fundings()).isEmpty();
		assertThat(result.pageInfo().totalElements()).isEqualTo(0L);
	}

	private Funding createFunding(Long amount) {
		Funding funding = Funding.createFunding(
			UUID.randomUUID(),
			userId,
			UUID.randomUUID().toString(),
			amount
		);

		try {
			java.lang.reflect.Field idField = Funding.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(funding, UUID.randomUUID());

			funding.completeFunding(UUID.randomUUID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return funding;
	}
}


