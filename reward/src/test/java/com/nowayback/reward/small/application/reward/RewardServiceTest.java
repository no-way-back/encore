package com.nowayback.reward.small.application.reward;

import com.nowayback.reward.application.reward.RewardService;
import com.nowayback.reward.application.reward.command.RewardCreateCommand;
import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nowayback.reward.fixture.RewardFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @InjectMocks
    private RewardService rewardService;

    @Nested
    @DisplayName("리워드 생성 테스트")
    class CreateRewardsForProject {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("단일 리워드 생성 성공")
            void createSingleReward() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = List.of(createRequest());

                when(rewardRepository.save(any(Rewards.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                List<Rewards> result = rewardService.createRewardsForProject(projectId, creatorId, requests);

                // then
                assertThat(result).hasSize(1);
                assertThat(result.get(0).getName()).isEqualTo("테스트 리워드");
                verify(rewardRepository, times(1)).save(any(Rewards.class));
            }

            @Test
            @DisplayName("여러 리워드 생성 성공")
            void createMultipleRewards() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = List.of(
                        createRequest("티셔츠", 25000L, 100),
                        createRequest("입장권", 30000L, 200),
                        createRequest("응원봉", 15000L, 150)
                );

                when(rewardRepository.save(any(Rewards.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                List<Rewards> result = rewardService.createRewardsForProject(projectId, creatorId, requests);

                // then
                assertThat(result).hasSize(3);
                assertThat(result).extracting("name")
                        .containsExactly("티셔츠", "입장권", "응원봉");
                verify(rewardRepository, times(3)).save(any(Rewards.class));
            }

            @Test
            @DisplayName("옵션이 있는 리워드 생성 성공")
            void createRewardWithOptions() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = List.of(createRequestWithOptions());

                when(rewardRepository.save(any(Rewards.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                List<Rewards> result = rewardService.createRewardsForProject(projectId, creatorId, requests);

                // then
                assertThat(result).hasSize(1);
                assertThat(result.get(0).getOptionList()).hasSize(3);
                verify(rewardRepository, times(1)).save(any(Rewards.class));
            }

            @Test
            @DisplayName("최대 개수(10개) 리워드 생성 성공")
            void createMaxCountRewards() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = createRequests(10);

                when(rewardRepository.save(any(Rewards.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                List<Rewards> result = rewardService.createRewardsForProject(projectId, creatorId, requests);

                // then
                assertThat(result).hasSize(10);
                verify(rewardRepository, times(10)).save(any(Rewards.class));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("리워드 개수 초과 시 예외 발생")
            void rewardCountExceeded() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = createRequests(11);

                // when & then
                assertThatThrownBy(() -> rewardService.createRewardsForProject(projectId, creatorId, requests))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.REWARD_COUNT_EXCEEDED);

                verify(rewardRepository, never()).save(any());
            }

            @Test
            @DisplayName("리워드 개수 초과 시 저장 안 함")
            void noSaveWhenCountExceeded() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = createRequests(15);

                // when & then
                assertThatThrownBy(() -> rewardService.createRewardsForProject(projectId, creatorId, requests))
                        .isInstanceOf(RewardException.class);

                verifyNoInteractions(rewardRepository);
            }

            @Test
            @DisplayName("금액이 최소값 미만일 때 예외 발생")
            void priceBelowMinimum() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = List.of(createRequest("리워드", 500L, 100));

                // when & then
                assertThatThrownBy(() -> rewardService.createRewardsForProject(projectId, creatorId, requests))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.PRICE_BELOW_MINIMUM);

                verify(rewardRepository, never()).save(any());
            }

            @Test
            @DisplayName("재고가 최소값 미만일 때 예외 발생")
            void stockBelowMinimum() {
                // given
                UUID projectId = UUID.randomUUID();
                UUID creatorId = UUID.randomUUID();
                List<RewardCreateCommand> requests = List.of(createRequest("리워드", 25000L, 0));

                // when & then
                assertThatThrownBy(() -> rewardService.createRewardsForProject(projectId, creatorId, requests))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.STOCK_BELOW_MINIMUM);

                verify(rewardRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("리워드 수정 테스트")
    class UpdateReward {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("리워드 수정 성공")
            void updateReward() {
                // given
                UUID rewardId = UUID.randomUUID();
                Rewards existingReward = Rewards.create(createCommand());
                UpdateRewardCommand command = createUpdateCommand(rewardId);

                when(rewardRepository.findById(rewardId))
                        .thenReturn(Optional.of(existingReward));

                // when
                rewardService.update(command);

                // then
                verify(rewardRepository, times(1)).findById(rewardId);
                assertThat(existingReward.getName()).isEqualTo("수정된 리워드");
                assertThat(existingReward.getPrice().getAmount()).isEqualTo(30000);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("존재하지 않는 리워드 수정 시 예외 발생")
            void updateNonExistentReward() {
                // given
                UUID rewardId = UUID.randomUUID();
                UpdateRewardCommand command = createUpdateCommand(rewardId);

                when(rewardRepository.findById(rewardId))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> rewardService.update(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.REWARD_NOT_FOUND);
            }
        }
    }
}