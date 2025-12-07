package com.nowayback.reward.application.reward;

import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.application.reward.dto.RewardListResult;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.application.reward.command.RewardCreateCommand;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
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
    private final StockReservationRepository stockReservationRepository;

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
     * 리워드 목록 조회
     * - isDelete 상태는 포함하지 않고 반환
     * - SOLD_OUT 상태는 포함하여 반환
     */
    @Transactional(readOnly = true)
    public RewardListResult getRewardsForProject(UUID projectId) {
        List<Rewards> rewardList = rewardRepository.findAvailableReward(projectId);

        return RewardListResult.from(rewardList);
    }

    /**
     * 리워드 수정
     * 옵션 수정의 경우 내부 Cascade 설정으로 변경감지 활용
     * @param command 리워드, 리워드 옵션 수정 요청 데이터
     */
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

    /**
     * 재고 예약 (차감)
     * - 옵션이 있는 경우: 옵션의 재고 차감
     * - 옵션이 없는 경우: 리워드의 재고 차감
     * - 필수 옵션이 있는데 선택하지 않은 경우: 에러
     * - StockReservation 생성 (DEDUCTED 상태)
     * - 가격 정보 계산하여 반환
     */
    @Transactional
    public StockReserveResult reserveStock(StockReserveCommand command) {
        log.info("재고 예약 시작 - fundingId: {}, items: {}개",
                command.fundingId(), command.items().size()
        );

        List<StockReserveResult.ReservationWithPrice> reservations
                = command.items().stream()
                .map(item -> reserveStockForItem(command.fundingId(), item))
                .toList();

        StockReserveResult result = StockReserveResult.from(command.fundingId(), reservations);

        log.info("재고 예약 완료 - fundingId: {}, 예약: {}개, 총액: {}",
                command.fundingId(), reservations.size(), result.totalAmount());

        return result;
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

    // private 헬퍼 메서드

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

    /**
     * 개별 아이템의 재고 예약 처리
     * - 옵션 ID가 있으면: 옵션 재고 차감 및 옵션 포함 가격 계산
     * - 옵션 ID가 없으면: 필수 옵션 검증 후 리워드 재고 차감 및 기본 가격 계산
     *
     * @param fundingId 펀딩 ID
     * @param item 재고 차감할 아이템 정보 (리워드 ID, 옵션 ID, 수량)
     * @return 예약 정보와 가격 정보
     */
    private StockReserveResult.ReservationWithPrice reserveStockForItem(
            UUID fundingId,
            StockReserveCommand.StockReserveItemCommand item
    ) {
        Rewards reward = getById(item.rewardId());

        if (item.optionId() != null) {
            return reserveWithOption(fundingId, reward, item);
        }

        reward.validateRequiredOption();

        return reserveWithoutOption(fundingId, reward, item);
    }

    /**
     * 옵션이 존재할 경우
     * - 옵션의 재고 감소
     * - 리워드 가격 + 옵션 추가금액 반환
     * - 옵션 품절 시 옵션 상태 변경
     * - 모든 옵션 품절 시 리워드 상태 동기화
     */
    private StockReserveResult.ReservationWithPrice reserveWithOption(
            UUID fundingId,
            Rewards reward,
            StockReserveCommand.StockReserveItemCommand item
    ) {
        RewardOptions option = reward.findOption(item.optionId());
        option.decreaseStock(item.quantity());
        reward.syncStatus();

        Long itemAmount = option.calculateTotalAmount(item.quantity());

        StockReservation saveReservation = createAndSaveReservation(
                fundingId, reward.getId(), option.getId(), item.quantity()
        );

        log.info("재고 예약 완료 - fundingId: {}, rewardId: {}, optionId: {}, quantity: {}, optionStatus: {}, rewardStatus: {}",
                fundingId, reward.getId(), option.getId(), item.quantity(), option.getStatus(), reward.getStatus());

        return StockReserveResult.ReservationWithPrice
                .of(saveReservation, itemAmount);
    }

    /**
     * 옵션이 존재하지 않을 경우
     * - 리워드의 재고 감소
     * - 리워드 가격 반환
     * - 재고 0일 시 리워드 상태 변경
     */
    private StockReserveResult.ReservationWithPrice reserveWithoutOption(
            UUID fundingId,
            Rewards reward,
            StockReserveCommand.StockReserveItemCommand item
    ) {
        reward.decreaseStock(item.quantity());

        Long itemAmount = reward.calculateTotalAmount(item.quantity());
        StockReservation saveReservation = createAndSaveReservation(
                fundingId, reward.getId(), null, item.quantity()
        );

        log.info("재고 예약 완료 - fundingId: {}, rewardId: {}, quantity: {}, rewardStatus: {}",
                fundingId, reward.getId(), item.quantity(), reward.getStatus());

        return StockReserveResult.ReservationWithPrice
                .of(saveReservation, itemAmount);
    }

    /**
     * 재고 예약 엔티티 생성 및 저장
     */
    private StockReservation createAndSaveReservation(
            UUID fundingId,
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {
        StockReservation reservation = StockReservation.create(
                fundingId, rewardId, optionId, quantity
        );
        return stockReservationRepository.save(reservation);
    }
}