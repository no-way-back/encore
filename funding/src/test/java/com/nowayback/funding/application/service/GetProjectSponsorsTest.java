package com.nowayback.funding.application.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
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
import org.springframework.data.domain.PageRequest;

import com.nowayback.funding.application.funding.dto.command.GetProjectSponsorsCommand;
import com.nowayback.funding.application.funding.dto.result.GetProjectSponsorsResult;
import com.nowayback.funding.application.funding.service.FundingServiceImpl;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("프로젝트별 후원자 조회 테스트")
class GetProjectSponsorsTest {

	@InjectMocks
	private FundingServiceImpl fundingService;

	@Mock
	private FundingRepository fundingRepository;

	@Mock
	private FundingProjectStatisticsService fundingProjectStatisticsService;

	private UUID projectId;
	private UUID creatorId;
	private UUID otherUserId;

	@BeforeEach
	void setUp() {
		projectId = UUID.randomUUID();
		creatorId = UUID.randomUUID();
		otherUserId = UUID.randomUUID();
	}

	@Test
	@DisplayName("프로젝트 후원자 조회 성공")
	void getProjectSponsors_Success() throws Exception {
		// given
		GetProjectSponsorsCommand command = new GetProjectSponsorsCommand(
			projectId,
			creatorId,
			0,
			20
		);

		Funding funding1 = createFunding(projectId, UUID.randomUUID(), 15000L);
		Funding funding2 = createFunding(projectId, UUID.randomUUID(), 20000L);
		List<Funding> fundings = List.of(funding1, funding2);
		Page<Funding> fundingPage = new PageImpl<>(fundings, PageRequest.of(0, 20), 2);

		// 권한 체크 통과
		willDoNothing().given(fundingProjectStatisticsService)
			.validateProjectCreator(projectId, creatorId);

		given(fundingRepository.findProjectSponsors(
			eq(projectId),
			eq(FundingStatus.COMPLETED),
			any(PageRequest.class)
		)).willReturn(fundingPage);

		given(fundingRepository.sumAmountByProjectIdAndStatus(
			projectId,
			FundingStatus.COMPLETED
		)).willReturn(35000L);

		// when
		GetProjectSponsorsResult result = fundingService.getProjectSponsors(command);

		// then
		assertThat(result.projectId()).isEqualTo(projectId);
		assertThat(result.totalSponsors()).isEqualTo(2);
		assertThat(result.totalAmount()).isEqualTo(35000L);
		assertThat(result.sponsors()).hasSize(2);
		assertThat(result.pageInfo().page()).isEqualTo(0);
		assertThat(result.pageInfo().size()).isEqualTo(20);
		assertThat(result.pageInfo().totalElements()).isEqualTo(2);

		verify(fundingProjectStatisticsService).validateProjectCreator(projectId, creatorId);
		verify(fundingRepository).findProjectSponsors(
			eq(projectId),
			eq(FundingStatus.COMPLETED),
			any(PageRequest.class)
		);
		verify(fundingRepository).sumAmountByProjectIdAndStatus(projectId, FundingStatus.COMPLETED);
	}

	@Test
	@DisplayName("프로젝트 후원자 조회 성공 - 빈 결과")
	void getProjectSponsors_EmptyResult_Success() {
		// given
		GetProjectSponsorsCommand command = new GetProjectSponsorsCommand(
			projectId,
			creatorId,
			0,
			20
		);

		Page<Funding> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

		willDoNothing().given(fundingProjectStatisticsService)
			.validateProjectCreator(projectId, creatorId);

		given(fundingRepository.findProjectSponsors(
			eq(projectId),
			eq(FundingStatus.COMPLETED),
			any(PageRequest.class)
		)).willReturn(emptyPage);

		given(fundingRepository.sumAmountByProjectIdAndStatus(
			projectId,
			FundingStatus.COMPLETED
		)).willReturn(0L);

		// when
		GetProjectSponsorsResult result = fundingService.getProjectSponsors(command);

		// then
		assertThat(result.totalSponsors()).isEqualTo(0);
		assertThat(result.totalAmount()).isEqualTo(0L);
		assertThat(result.sponsors()).isEmpty();
	}

	@Test
	@DisplayName("프로젝트 후원자 조회 실패 - 프로젝트 없음")
	void getProjectSponsors_ProjectNotFound_ThrowsException() {
		// given
		GetProjectSponsorsCommand command = new GetProjectSponsorsCommand(
			projectId,
			creatorId,
			0,
			20
		);

		willThrow(new FundingException(PROJECT_NOT_FOUND))
			.given(fundingProjectStatisticsService)
			.validateProjectCreator(projectId, creatorId);

		// when & then
		assertThatThrownBy(() -> fundingService.getProjectSponsors(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(PROJECT_NOT_FOUND.getMessage());

		verify(fundingRepository, never()).findProjectSponsors(any(), any(), any());
	}

	@Test
	@DisplayName("프로젝트 후원자 조회 실패 - 권한 없음")
	void getProjectSponsors_Forbidden_ThrowsException() {
		// given
		GetProjectSponsorsCommand command = new GetProjectSponsorsCommand(
			projectId,
			otherUserId,  // 생성자가 아닌 사용자
			0,
			20
		);

		willThrow(new FundingException(FORBIDDEN_PROJECT_ACCESS))
			.given(fundingProjectStatisticsService)
			.validateProjectCreator(projectId, otherUserId);

		// when & then
		assertThatThrownBy(() -> fundingService.getProjectSponsors(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(FORBIDDEN_PROJECT_ACCESS.getMessage());

		verify(fundingRepository, never()).findProjectSponsors(any(), any(), any());
	}

	@Test
	@DisplayName("프로젝트 후원자 조회 성공 - 페이징 (2페이지)")
	void getProjectSponsors_Paging_Success() throws Exception {
		// given
		GetProjectSponsorsCommand command = new GetProjectSponsorsCommand(
			projectId,
			creatorId,
			1,  // 2페이지 (0-indexed)
			10  // 10개씩
		);

		// 2페이지에는 5개만 있다고 가정 (총 15개 중 11-15번째)
		Funding funding1 = createFunding(projectId, UUID.randomUUID(), 10000L);
		Funding funding2 = createFunding(projectId, UUID.randomUUID(), 12000L);
		Funding funding3 = createFunding(projectId, UUID.randomUUID(), 15000L);
		Funding funding4 = createFunding(projectId, UUID.randomUUID(), 20000L);
		Funding funding5 = createFunding(projectId, UUID.randomUUID(), 8000L);

		List<Funding> fundings = List.of(funding1, funding2, funding3, funding4, funding5);
		Page<Funding> fundingPage = new PageImpl<>(fundings, PageRequest.of(1, 10), 15);

		willDoNothing().given(fundingProjectStatisticsService)
			.validateProjectCreator(projectId, creatorId);

		given(fundingRepository.findProjectSponsors(
			eq(projectId),
			eq(FundingStatus.COMPLETED),
			any(PageRequest.class)
		)).willReturn(fundingPage);

		given(fundingRepository.sumAmountByProjectIdAndStatus(
			projectId,
			FundingStatus.COMPLETED
		)).willReturn(150000L);

		// when
		GetProjectSponsorsResult result = fundingService.getProjectSponsors(command);

		// then
		assertThat(result.sponsors()).hasSize(5);  // 2페이지에는 5개
		assertThat(result.pageInfo().page()).isEqualTo(1);
		assertThat(result.pageInfo().size()).isEqualTo(10);
		assertThat(result.pageInfo().totalElements()).isEqualTo(15);
		assertThat(result.pageInfo().totalPages()).isEqualTo(2);  // ceil(15/10) = 2
	}

	// Helper Methods
	private Funding createFunding(UUID projectId, UUID userId, Long amount) throws Exception {
		Funding funding = Funding.createFunding(
			projectId,
			userId,
			UUID.randomUUID().toString(),
			amount
		);

		// Reflection으로 ID 설정
		setField(funding, UUID.randomUUID());

		// COMPLETED 상태로 변경
		funding.completeFunding(UUID.randomUUID(), UUID.randomUUID());

		return funding;
	}

	private void setField(Object target, Object value) throws Exception {
		java.lang.reflect.Field field = target.getClass().getDeclaredField("id");
		field.setAccessible(true);
		field.set(target, value);
	}
}
