package com.nowayback.reward.domain.entity;

import com.nowayback.reward.domain.shared.BaseEntity;
import com.nowayback.reward.domain.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_reward_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardOptions extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "additional_price", nullable = false))
    private Money additionalPrice;

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "stock_quantity", nullable = false))
    private Stock stock;

    @Column(nullable = false)
    private Boolean isRequired;

    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Rewards reward;
}