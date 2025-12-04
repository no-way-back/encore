package com.nowayback.reward.domain.reward.entity;

import com.nowayback.reward.application.reward.command.UpdateRewardOptionCommand;
import com.nowayback.reward.domain.reward.vo.Money;
import com.nowayback.reward.domain.reward.vo.SaleStatus;
import com.nowayback.reward.domain.reward.vo.Stock;
import com.nowayback.reward.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.nowayback.reward.domain.reward.vo.SaleStatus.*;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SaleStatus status = AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Rewards reward;

    /**
     * 리워드 옵션 생성자
     */
    RewardOptions(String name, Money additionalPrice, Stock stock, Boolean isRequired, Integer displayOrder, Rewards reward) {
        this.name = name;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
        this.isRequired = isRequired;
        this.displayOrder = displayOrder;
        this.reward = reward;
    }

    public void update(UpdateRewardOptionCommand command) {
        if (command.name() != null) {
            this.name = command.name();
        }
        if (command.additionalPrice() != null) {
            this.additionalPrice = Money.of(command.additionalPrice());
        }
        if (command.stockQuantity() != null) {
            this.stock = Stock.of(command.stockQuantity());
        }
        if (command.isRequired() != null) {
            this.isRequired = command.isRequired();
        }
        if (command.displayOrder() != null) {
            this.displayOrder = command.displayOrder();
        }
    }

    /**
     * 재고 차감
     * - 재고가 0이되면 품절 상태로 변경
     */
    public void decreaseStock(Integer quantity) {
        this.stock = this.stock.decrease(quantity);

        if (this.stock.isSoldOut()) {
            this.status = SOLD_OUT;
        }
    }

    /**
     * 재고 복원
     * - 품절 상태 였다면 판매 가능 상태로 변경
     */
    public void restoreStock(Integer quantity) {
        this.stock = this.stock.restore(quantity);

        if (this.status == SOLD_OUT && this.stock.hasStock()) {
            this.status = AVAILABLE;
        }
    }

    /**
     * 옵션 포함 총 가격 계산 (리워드 가격 + 옵션 추가금)
     */
    public Integer calculateTotalAmount(Integer quantity) {
        Integer unitPrice = this.reward.getPrice().getAmount() + this.additionalPrice.getAmount();
        return unitPrice * quantity;
    }

    /**
     * 판매 가능 여부 확인
     */
    public boolean isAvailableForSale() {
        return this.status == AVAILABLE && this.stock.getQuantity() > 0;
    }
}