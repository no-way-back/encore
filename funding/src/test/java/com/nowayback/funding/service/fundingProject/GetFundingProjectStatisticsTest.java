package com.nowayback.funding.service.fundingProject;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsServiceImpl;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("프로젝트 통계 조회 테스트")
class GetFundingProjectStatisticsTest {

	@InjectMocks
	private FundingProjectStatisticsServiceImpl fundingProjectStatisticsService;

	@Mock
	private FundingProjectStatisticsRepository fundingProjectStatisticsRepository;

	private UUID projectId;
	private UUID creatorId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@BeforeEach
	void setUp() {
		projectId = UUID.randomUUID();
		creatorId = UUID.randomUUID();
		startDate = LocalDateTime.now();
		endDate = LocalDateTime.now().plusDays(30);
	}

	@Test
	@DisplayName("프로젝트 통계 조회 성공 - 진행 중")
	void getFundingProjectStatistics_Processing_Success() {
		// given
		FundingProjectStatistics stats = FundingProjectStatistics.create(
			projectId,
			creatorId,
			5000000L,
			startDate,
			endDate
		);

		// 후원 금액 증가 (목표의 70% 달성)
		stats.increaseFunding(3500000L);

		given(fundingProjectStatisticsRepository.findByProjectId(projectId))
			.willReturn(Optional.of(stats));

		// when
		FundingProjectStatisticsResult result =
			fundingProjectStatisticsService.getFundingProjectStatistics(projectId);

		// then
		assertThat(result.projectId()).isEqualTo(projectId);
		assertThat(result.targetAmount()).isEqualTo(5000000L);
		assertThat(result.currentAmount()).isEqualTo(3500000L);
		assertThat(result.achievementRate()).isEqualTo(70.0);
		assertThat(result.participantCount()).isEqualTo(1);
		assertThat(result.status()).isEqualTo("PROCESSING");
		assertThat(result.startDate()).isEqualTo(startDate);
		assertThat(result.endDate()).isEqualTo(endDate);

		verify(fundingProjectStatisticsRepository).findByProjectId(projectId);
	}

	@Test
	@DisplayName("프로젝트 통계 조회 성공 - 목표 달성")
	void getFundingProjectStatistics_TargetAchieved_Success() {
		// given
		FundingProjectStatistics stats = FundingProjectStatistics.create(
			projectId,
			creatorId,
			5000000L,
			startDate,
			endDate
		);

		// 목표 금액 초과 달성 (120%)
		stats.increaseFunding(6000000L);

		given(fundingProjectStatisticsRepository.findByProjectId(projectId))
			.willReturn(Optional.of(stats));

		// when
		FundingProjectStatisticsResult result =
			fundingProjectStatisticsService.getFundingProjectStatistics(projectId);

		// then
		assertThat(result.currentAmount()).isEqualTo(6000000L);
		assertThat(result.achievementRate()).isEqualTo(120.0);
		assertThat(result.participantCount()).isEqualTo(1);

		verify(fundingProjectStatisticsRepository).findByProjectId(projectId);
	}

	@Test
	@DisplayName("프로젝트 통계 조회 성공 - 후원 없음 (0%)")
	void getFundingProjectStatistics_NoFunding_Success() {
		// given
		FundingProjectStatistics stats = FundingProjectStatistics.create(
			projectId,
			creatorId,
			5000000L,
			startDate,
			endDate
		);

		given(fundingProjectStatisticsRepository.findByProjectId(projectId))
			.willReturn(Optional.of(stats));

		// when
		FundingProjectStatisticsResult result =
			fundingProjectStatisticsService.getFundingProjectStatistics(projectId);

		// then
		assertThat(result.currentAmount()).isEqualTo(0L);
		assertThat(result.achievementRate()).isEqualTo(0.0);
		assertThat(result.participantCount()).isEqualTo(0);
		assertThat(result.status()).isEqualTo("PROCESSING");

		verify(fundingProjectStatisticsRepository).findByProjectId(projectId);
	}

	@Test
	@DisplayName("프로젝트 통계 조회 성공 - 여러 번 후원")
	void getFundingProjectStatistics_MultipleFundings_Success() {
		// given
		FundingProjectStatistics stats = FundingProjectStatistics.create(
			projectId,
			creatorId,
			5000000L,
			startDate,
			endDate
		);

		// 여러 번 후원 (총 3번, 2,500,000원)
		stats.increaseFunding(1000000L);
		stats.increaseFunding(500000L);
		stats.increaseFunding(1000000L);

		given(fundingProjectStatisticsRepository.findByProjectId(projectId))
			.willReturn(Optional.of(stats));

		// when
		FundingProjectStatisticsResult result =
			fundingProjectStatisticsService.getFundingProjectStatistics(projectId);

		// then
		assertThat(result.currentAmount()).isEqualTo(2500000L);
		assertThat(result.achievementRate()).isEqualTo(50.0);
		assertThat(result.participantCount()).isEqualTo(3);

		verify(fundingProjectStatisticsRepository).findByProjectId(projectId);
	}

	@Test
	@DisplayName("프로젝트 통계 조회 실패 - 존재하지 않는 프로젝트")
	void getFundingProjectStatistics_ProjectNotFound_ThrowsException() {
		// given
		given(fundingProjectStatisticsRepository.findByProjectId(projectId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			fundingProjectStatisticsService.getFundingProjectStatistics(projectId))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(PROJECT_NOT_FOUND.getMessage());

		verify(fundingProjectStatisticsRepository).findByProjectId(projectId);
	}

	@Test
	@DisplayName("프로젝트 통계 조회 성공 - SCHEDULED 상태")
	void getFundingProjectStatistics_Scheduled_Success() {
		// given
		LocalDateTime futureStartDate = LocalDateTime.now().plusDays(7);
		LocalDateTime futureEndDate = LocalDateTime.now().plusDays(37);

		FundingProjectStatistics stats = FundingProjectStatistics.create(
			projectId,
			creatorId,
			5000000L,
			futureStartDate,
			futureEndDate
		);

		given(fundingProjectStatisticsRepository.findByProjectId(projectId))
			.willReturn(Optional.of(stats));

		// when
		FundingProjectStatisticsResult result =
			fundingProjectStatisticsService.getFundingProjectStatistics(projectId);

		// then
		assertThat(result.status()).isEqualTo("SCHEDULED");
		assertThat(result.currentAmount()).isEqualTo(0L);
		assertThat(result.participantCount()).isEqualTo(0);

		verify(fundingProjectStatisticsRepository).findByProjectId(projectId);
	}
}
