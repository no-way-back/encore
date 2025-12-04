package com.nowayback.reward.application.reward;

import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.repository.StockReservationRepository;
import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.vo.FundingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.REWARD_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardStockService {

    private final RewardRepository rewardRepository;
    private final StockReservationRepository stockReservationRepository;

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

    /**
     * 재고 복원 (증가)
     * - fundingId로 모든 StockReservation 조회
     * - 옵션이 있는 경우: 옵션의 재고 복원
     * - 옵션이 없는 경우: 리워드의 재고 복원
     * - StockReservation 상태를 RESTORED로 변경
     */
    @Transactional
    public void restoreStock(UUID fundingId) {
        log.info("재고 복원 시작 - fundingId: {}", fundingId);

        List<StockReservation> reservations = stockReservationRepository.findByFundingId(FundingId.of(fundingId));

        if (reservations.isEmpty()) {
            log.warn("펀딩에 해당하는 예약이 없습니다 - fundingId: {}", fundingId);
            return;
        }

        log.info("복원 대상 예약 수: {}", reservations.size());

        reservations.forEach(this::restoreStockForReservation);

        log.info("재고 복원 완료 - fundingId: {}, 처리된 예약 수: {}", fundingId, reservations.size());
    }

    // ========== private 메서드 ==========

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
     * 개별 예약의 재고 복원 처리
     * - 이미 복원된 예약은 건너뜀
     * - 옵션 ID가 있으면: 옵션 재고 복원
     * - 옵션 ID가 없으면: 리워드 재고 복원
     * - 품절 상태였다면 상태 동기화
     */
    private void restoreStockForReservation(StockReservation reservation) {
        if (reservation.isRestored()) {
            log.warn("이미 복원된 예약 건너뜀 - reservationId: {}", reservation.getId());
            return;
        }

        Rewards reward = getById(reservation.getRewardId().getId());

        if (reservation.getOptionId() != null) {
            restoreWithOption(reward, reservation);
        } else {
            restoreWithoutOption(reward, reservation);
        }

        reservation.restore();
    }

    /**
     * 옵션 재고 복원
     * - 옵션의 재고 증가
     * - 품절 → 판매중 상태 변경
     * - 리워드 상태 동기화
     */
    private void restoreWithOption(Rewards reward, StockReservation reservation) {
        RewardOptions option = reward.findOption(reservation.getOptionId().getId());
        option.restoreStock(reservation.getQuantity());
        reward.syncStatus();

        log.info("옵션 재고 복원 완료 - fundingId: {}, rewardId: {}, optionId: {}, quantity: {}, optionStatus: {}, rewardStatus: {}",
                reservation.getFundingId(), reward.getId(), option.getId(),
                reservation.getQuantity(), option.getStatus(), reward.getStatus());
    }

    /**
     * 리워드 재고 복원
     * - 리워드의 재고 증가
     * - 품절 → 판매중 상태 변경
     */
    private void restoreWithoutOption(Rewards reward, StockReservation reservation) {
        reward.restoreStock(reservation.getQuantity());

        log.info("리워드 재고 복원 완료 - fundingId: {}, rewardId: {}, quantity: {}, rewardStatus: {}",
                reservation.getFundingId(), reward.getId(),
                reservation.getQuantity(), reward.getStatus());
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

    private Rewards getById(UUID rewardId) {
        return rewardRepository.findById(rewardId).orElseThrow(
                () -> new RewardException(REWARD_NOT_FOUND)
        );
    }
}