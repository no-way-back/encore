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
                @Index(name = "idx_stock_reservation_expires_at", columnList = "expires_at, status")
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

    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    /**
     * 재고 예약 생성
     */
    public static StockReservation create(
            UUID userId,
            UUID fundingId,
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {
        StockReservation reservation = new StockReservation();
        reservation.userId = UserId.of(userId);
        reservation.fundingId = FundingId.of(fundingId);
        reservation.rewardId = RewardId.of(rewardId);
        reservation.optionId = optionId != null ? OptionId.of(optionId) : null;
        reservation.quantity = quantity;
        reservation.expiresAt = LocalDateTime.now().plusMinutes(10);
        reservation.status = ReservationStatus.PENDING;
        return reservation;
    }

    /**
     * 예약 확정 (펀딩 생성 성공 시)
     */
    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태만 확정할 수 있습니다");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.expiresAt = null;
    }

    /**
     * 재고 복원 (펀딩 실패/취소 시)
     */
    public void restore() {
        if (this.status == ReservationStatus.RESTORED) {
            throw new IllegalStateException("이미 복원된 예약입니다");
        }
        this.status = ReservationStatus.RESTORED;
        this.expiresAt = null;
    }

    /**
     * 복원 여부 확인
     */
    public boolean isRestored() {
        return this.status == ReservationStatus.RESTORED;
    }

    /**
     * 재고 선점 확인
     */
    public boolean isPending() {
        return this.status == ReservationStatus.PENDING;
    }

    /**
     * 선점 만료 시간 확인
     */
    public boolean isExpired() {
        return isPending() && expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}