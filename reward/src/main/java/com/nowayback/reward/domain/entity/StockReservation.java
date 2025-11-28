package com.nowayback.reward.domain.entity;

import com.nowayback.reward.domain.shared.BaseEntity;
import com.nowayback.reward.domain.vo.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_stock_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID fundingId;

    @Column(nullable = false)
    private UUID rewardId;

    private UUID optionId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;
}