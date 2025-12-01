package com.nowayback.reward.application.reward;

import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.application.reward.command.RewardCreateCommand;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;

    private static final int MAX_REWARD_COUNT = 10;

    /**
     * 프로젝트에 속한 리워드 생성
     * 각 리워드에 옵션이 있는 경우 Cascade 설정으로 한 번에 같이 생성.
     *
     * @param projectId 프로젝트 ID
     * @param commandList 리워드 생성 요청 목록
     * @return 생성된 Rewards 엔티티 목록
     */
    @Transactional
    public List<Rewards> createRewardsForProject(UUID projectId, UUID creatorId, List<RewardCreateCommand> commandList) {
        log.info("프로젝트 {} 리워드 생성 시작 - {}개", projectId, commandList.size());

        if (commandList.size() > MAX_REWARD_COUNT) {
            throw new RewardException(REWARD_COUNT_EXCEEDED);
        }

        List<Rewards> createdRewards = commandList.stream()
                .map(request -> createReward(projectId, creatorId, request))
                .toList();

        log.info("프로젝트 {} 총 {}개 리워드 생성 완료", projectId, createdRewards.size());
        return createdRewards;
    }

    /**
     * 리워드 수정
     * 옵션 수정의 경우 내부 Cascade 설정으로 변경감지 활용
     * @param command 리워드, 리워드 옵션 수정 요청 데이터
     */
    @Transactional
    public void update(UpdateRewardCommand command) {
        Rewards reward = rewardRepository.findById(command.rewardId()).orElseThrow(
                () -> new RewardException(REWARD_NOT_FOUND)
        );

        reward.update(command);
    }

    /**
     * 단일 리워드를 생성 후 옵션 추가
     *
     * @param projectId 프로젝트 ID
     * @param command 리워드 생성 요청
     * @return 생성된 Rewards 엔티티
     * @throws RewardException 옵션 검증 실패 시
     */
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