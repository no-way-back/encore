package com.nowayback.reward.domain.reward.entity;

import com.nowayback.reward.domain.reward.vo.*;
import com.nowayback.reward.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rewards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 1000)
    private String description;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false))
    private Money price;

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "stock_quantity", nullable = false))
    private Stock stock;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "shippingFee", column = @Column(name = "shipping_fee", nullable = false)),
            @AttributeOverride(name = "freeShippingAmount", column = @Column(name = "free_shipping_amount"))
    })
    private ShippingPolicy shippingPolicy;

    private Integer purchaseLimitPerPerson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RewardType rewardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SaleStatus status;

    @OneToMany(
            mappedBy = "reward",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<RewardOptions> optionList = new ArrayList<>();

}