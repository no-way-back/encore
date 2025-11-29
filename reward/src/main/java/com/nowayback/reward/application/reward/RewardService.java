package com.nowayback.reward.application.reward;

import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import com.nowayback.reward.infrastructure.kafka.dto.project.request.RewardCreateRequest;
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
     * @param requests 리워드 생성 요청 목록
     * @return 생성된 Rewards 엔티티 목록
     */
    @Transactional
    public List<Rewards> createRewardsForProject(UUID projectId, UUID creatorId, List<RewardCreateRequest> requests) {
        log.info("프로젝트 {} 리워드 생성 시작 - {}개", projectId, requests.size());

        if (requests.size() > MAX_REWARD_COUNT) {
            throw new RewardException(REWARD_COUNT_EXCEEDED);
        }

        List<Rewards> createdRewards = requests.stream()
                .map(request -> createReward(projectId, creatorId, request))
                .toList();

        log.info("프로젝트 {} 총 {}개 리워드 생성 완료", projectId, createdRewards.size());
        return createdRewards;
    }

    /**
     * 단일 리워드를 생성 후 옵션 추가
     *
     * @param projectId 프로젝트 ID
     * @param request 리워드 생성 요청
     * @return 생성된 Rewards 엔티티
     * @throws RewardException 옵션 검증 실패 시
     */
    private Rewards createReward(UUID projectId, UUID creatorId, RewardCreateRequest request) {
        CreateRewardCommand command = CreateRewardCommand.from(projectId, creatorId, request);

        Rewards reward = Rewards.create(command);
        reward.addOptionList(command.options());

        Rewards savedReward = rewardRepository.save(reward);

        log.info("리워드 생성 완료: {} - {} (옵션 {}개)",
                savedReward.getId(),
                savedReward.getName(),
                savedReward.getOptionList().size());

        return savedReward;
    }
}