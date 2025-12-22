package com.nowayback.reward.application.reward;

import com.nowayback.reward.application.idempotentkey.repository.IdempotentKeyRepository;
import com.nowayback.reward.application.outbox.event.OutboxEventPublisher;
import com.nowayback.reward.application.reward.command.RewardCreateCommand;
import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.application.reward.dto.RewardCreationResult;
import com.nowayback.reward.application.reward.dto.RewardListResult;
import com.nowayback.reward.application.reward.repository.RewardRepository;
import com.nowayback.reward.domain.idempotentkey.IdempotentKeys;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.outbox.vo.AggregateType;
import com.nowayback.reward.domain.outbox.vo.EventDestination;
import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.entity.Rewards;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.REWARD_COUNT_EXCEEDED;
import static com.nowayback.reward.domain.exception.RewardErrorCode.REWARD_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    private static final int MAX_REWARD_COUNT = 10;

    @Transactional
    public List<Rewards> createRewardsForProject(
            UUID eventId,
            UUID projectId,
            UUID creatorId,
            List<RewardCreateCommand> commandList
    ) {

        log.info("프로젝트 {} 리워드 생성 시작 - {}개, eventId: {}",
                projectId, commandList.size(), eventId);

        try {
            if (commandList.size() > MAX_REWARD_COUNT) {
                throw new RewardException(REWARD_COUNT_EXCEEDED);
            }

            List<Rewards> createdRewards = commandList.stream()
                    .map(request -> createReward(projectId, creatorId, request))
                    .toList();

            log.info("프로젝트 {} 총 {}개 리워드 생성 완료", projectId, createdRewards.size());

            saveSuccessOutbox(projectId);

            return createdRewards;
        } catch (Exception e) {
            saveFailureOutbox(projectId);
            throw e;
        }

    }

    @Transactional(readOnly = true)
    public RewardListResult getRewardsForProject(UUID projectId) {
        List<Rewards> rewardList = rewardRepository.findAvailableReward(projectId);
        return RewardListResult.from(rewardList);
    }

    @Transactional
    public void update(UpdateRewardCommand command) {
        Rewards reward = getById(command.rewardId());
        reward.update(command);
    }

    @Transactional
    public void delete(UUID rewardId) {
        Rewards reward = getById(rewardId);
        reward.delete();
    }

    public boolean isTicketType(UUID rewardId) {
        Rewards reward = getById(rewardId);
        return reward.isTicketType();
    }

    public Rewards getById(UUID rewardId) {
        return rewardRepository.findById(rewardId).orElseThrow(
                () -> new RewardException(REWARD_NOT_FOUND)
        );
    }

    private void saveSuccessOutbox(UUID projectId) {
        outboxEventPublisher.publish(
                EventType.REWARD_CREATION_SUCCESS,
                EventDestination.PROJECT_SERVICE,
                RewardCreationResult.success(projectId),
                AggregateType.PROJECT,
                projectId
        );
    }

    private void saveFailureOutbox(UUID projectId) {
        outboxEventPublisher.publish(
                EventType.REWARD_CREATION_FAILED,
                EventDestination.PROJECT_SERVICE,
                RewardCreationResult.failure(projectId),
                AggregateType.PROJECT,
                projectId
        );
    }

    private Rewards createReward(UUID projectId, UUID creatorId, RewardCreateCommand command) {
        CreateRewardCommand createRewardCommand = CreateRewardCommand.from(projectId, creatorId, command);

        Rewards reward = Rewards.create(createRewardCommand);
        reward.addOptionList(createRewardCommand.options());

        Rewards savedReward = rewardRepository.save(reward);

        log.info("리워드 생성 완료: {} - {} (옵션 {}개)",
                savedReward.getId(),
                savedReward.getName(),
                savedReward.getOptionList().size());

        return savedReward;
    }
}