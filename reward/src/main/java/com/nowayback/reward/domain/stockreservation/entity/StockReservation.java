package com.nowayback.reward.domain.stockreservation.entity;

import com.nowayback.reward.domain.shared.BaseEntity;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import com.nowayback.reward.domain.vo.FundingId;
import com.nowayback.reward.domain.vo.OptionId;
import com.nowayback.reward.domain.vo.RewardId;
import com.nowayback.reward.domain.vo.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "p_stock_reservations",
        indexes = {
                @Index(name = "idx_stock_reservation_funding_id", columnList = "funding_id"),
                @Index(name = "idx_stock_reservation_reward_id", columnList = "reward_id"),
                @Index(name = "idx_stock_reservation_funding_status", columnList = "funding_id, status"),
                @Index(name = "idx_stock_reservation_status_expires_at", columnList = "status, expires_at")
        }
)@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private UserId userId;

    @Embedded
    private FundingId fundingId;

    @Embedded
    private RewardId rewardId;

    private OptionId optionId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    /**
     * PENDING 예약의 만료 시각. 이 시각까지 결제가 확정되지 않으면 스케줄러가 재고를 복원한다.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * 재고 임시 예약 생성 (PENDING)
     * - 재고는 이미 차감된 상태이고, 결제 성공 시 확정(CONFIRMED), 실패·만료 시 복원(RESTORED)된다
     */
    public static StockReservation create(
            UUID userId,
            UUID fundingId,
            UUID rewardId,
            UUID optionId,
            Integer quantity,
            LocalDateTime expiresAt
    ) {
        StockReservation reservation = new StockReservation();
        reservation.userId = UserId.of(userId);
        reservation.fundingId = FundingId.of(fundingId);
        reservation.rewardId = RewardId.of(rewardId);
        reservation.optionId = optionId != null ? OptionId.of(optionId) : null;
        reservation.quantity = quantity;
        reservation.status = ReservationStatus.PENDING;
        reservation.expiresAt = expiresAt;
        return reservation;
    }

    /**
     * 예약 확정 (결제 성공 시)
     * - 같은 이벤트가 다시 와도 안전하도록 이미 확정된 예약은 그대로 둔다
     */
    public void confirm() {
        if (this.status == ReservationStatus.CONFIRMED) {
            return;
        }
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태만 확정할 수 있습니다 - 현재 상태: " + this.status);
        }
        this.status = ReservationStatus.CONFIRMED;
        this.expiresAt = null;
    }

    /**
     * 재고 복원 처리
     */
    public void restore() {
        if (this.status == ReservationStatus.RESTORED) {
            throw new IllegalStateException("이미 복원된 예약입니다");
        }
        this.status = ReservationStatus.RESTORED;
        this.expiresAt = null;
    }

    public boolean isPending() {
        return this.status == ReservationStatus.PENDING;
    }

    /**
     * 복원 여부 확인
     */
    public boolean isRestored() {
        return this.status == ReservationStatus.RESTORED;
    }
}