package com.nowayback.reward.domain.reward.entity;

import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.application.reward.command.UpdateRewardOptionCommand;
import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.command.CreateRewardOptionCommand;
import com.nowayback.reward.domain.reward.vo.*;
import com.nowayback.reward.domain.shared.BaseEntity;
import com.nowayback.reward.domain.vo.CreatorId;
import com.nowayback.reward.domain.vo.ProjectId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Entity
@Table(name = "p_rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rewards extends BaseEntity {

    private static final int MAX_OPTION_COUNT = 20;
    private static final int MINIMUM_AMOUNT = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private ProjectId projectId;

    @Embedded
    private CreatorId creatorId;

    @Column(nullable = false, length = 200)
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

    @Column(nullable = false)
    private boolean isDeleted;

    @OneToMany(
            mappedBy = "reward",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<RewardOptions> optionList = new ArrayList<>();

    /**
     * 리워드 생성 정적 팩토리 메서드
     *
     * @param command 리워드 생성에 필요한 정보를 담은 커맨드 객체
     * @return 생성된 Rewards 엔티티
     */
    public static Rewards create(CreateRewardCommand command) {
        Money price = Money.of(command.price());

        if (price.getAmount() < MINIMUM_AMOUNT) {
            throw new RewardException(PRICE_BELOW_MINIMUM);
        }

        return new Rewards(
                ProjectId.of(command.projectId()),
                CreatorId.of(command.creatorId()),
                command.name(),
                command.description(),
                price,
                Stock.of(command.stockQuantity()),
                ShippingPolicy.of(command.shippingFee(), command.freeShippingAmount()),
                command.purchaseLimitPerPerson(),
                command.rewardType(),
                SaleStatus.AVAILABLE
        );
    }

    /**
     * 리워드 수정
     * - null 값은 기존값 유지
     */
    public void update(UpdateRewardCommand command) {
        updateFields(command);
        updateShippingPolicy(command);
        updateOptions(command.options());
    }

    /**
     * 옵션 수정
     */
    private void updateOptions(List<UpdateRewardOptionCommand> commands) {
        if (commands == null) {
            return;
        }

        for (UpdateRewardOptionCommand command : commands) {
            RewardOptions option = findOption(command.optionId());

            if (command.name() != null && !command.name().equals(option.getName())) {
                validateOptionName(command.name());
            }
            if (command.displayOrder() != null && !command.displayOrder().equals(option.getDisplayOrder())) {
                validateDisplayOrder(command.displayOrder());
            }

            option.update(command);
        }
    }

    /**
     * 여러 옵션을 한 번에 추가
     * 각 옵션에 대해 도메인 규칙 검증 수행
     *
     * @param commands 옵션 생성 커맨드 목록
     * @throws RewardException 옵션 검증 실패 시
     */
    public void addOptionList(List<CreateRewardOptionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        if (commands.size() > MAX_OPTION_COUNT) {
            throw new RewardException(OPTION_COUNT_EXCEEDED);
        }

        commands.forEach(this::addOption);
    }

    /**
     * 연관관계 편의 메서드
     *
     * 도메인 규칙 검증
     * - 옵션명 중복 방지
     * - 표시 순서 충돌 방지
     *
     * @param command 옵션 생성에 필요한 정보를 담은 커맨드 객체
     * @return 생성된 RewardOptions 엔티티
     * @throws RewardException 옵션명 중복 또는 표시 순서 충돌 시
     */
    public RewardOptions addOption(CreateRewardOptionCommand command) {
        validateOptionName(command.name());
        validateDisplayOrder(command.displayOrder());

        RewardOptions option = new RewardOptions(
                command.name(),
                Money.of(command.additionalPrice()),
                Stock.of(command.stockQuantity()),
                command.isRequired(),
                command.displayOrder(),
                this
        );

        this.optionList.add(option);
        return option;
    }

    /**
     * 옵션명 중복 검증
     * 동일한 리워드 내에서 같은 이름의 옵션이 존재하는지 확인
     *
     * @param name 검증할 옵션명
     * @throws RewardException 중복된 옵션명이 존재할 경우
     */
    private void validateOptionName(String name) {
        boolean isDuplicate = this.optionList.stream()
                .anyMatch(option -> option.getName().equals(name));

        if (isDuplicate) {
            throw new RewardException(DUPLICATE_OPTION_NAME);
        }
    }

    /**
     * 표시 순서 충돌 검증
     * 동일한 리워드 내에서 같은 표시 순서를 가진 옵션이 존재하는지 확인
     *
     * @param displayOrder 검증할 표시 순서
     * @throws RewardException 충돌하는 표시 순서가 존재할 경우
     */
    private void validateDisplayOrder(Integer displayOrder) {
        boolean isConflict = this.optionList.stream()
                .anyMatch(option -> option.getDisplayOrder().equals(displayOrder));

        if (isConflict) {
            throw new RewardException(DUPLICATE_DISPLAY_ORDER);
        }
    }

    /**
     * 옵션 ID로 리워드의 옵션 조회
     */
    public RewardOptions findOption(UUID optionId) {
        return this.optionList.stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new RewardException(OPTION_NOT_FOUND));
    }

    /**
     * 리워드 삭제
     * - soft delete
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 옵션이 있는 리워드인지 확인
     * - UI 표현 확인 용도
     */
    public boolean hasOptions() {
        return !this.optionList.isEmpty();
    }

    /**
     * 재고 차감
     * 옵션이 없는 리워드만 재고가 0이면 SOLD_OUT 처리
     */
    public void decreaseStock(Integer quantity) {
        this.stock = this.stock.decrease(quantity);

        // 옵션이 없는 리워드만 자동 품절 처리
        if (!hasOptions() && this.stock.getQuantity() == 0) {
            this.status = SaleStatus.SOLD_OUT;
        }
    }

    /**
     * 필수 옵션 존재 여부 확인
     */
    public boolean hasRequiredOption() {
        return this.optionList.stream()
                .anyMatch(RewardOptions::getIsRequired);
    }

    /**
     * 필수 옵션 검증
     * 필수 옵션이 있는데 옵션을 선택하지 않은 경우 예외 발생
     */
    public void validateRequiredOption() {
        if (hasRequiredOption()) {
            throw new RewardException(REQUIRED_OPTION_NOT_SELECTED);
        }
    }

    /**
     * 리워드 총 금액 계산 (단가 * 수량)
     */
    public Integer calculateTotalAmount(Integer quantity) {
        return this.price.getAmount() * quantity;
    }

    /**
     * 리워드 상태 동기화
     * 모든 옵션이 품절되면 리워드도 SOLD_OUT 처리
     */
    public void syncStatus() {
        if (hasOptions() && areAllOptionsSoldOut()) {
            this.status = SaleStatus.SOLD_OUT;
        }
    }

    /**
     * 모든 옵션이 품절되었는지 확인
     */
    private boolean areAllOptionsSoldOut() {
        return this.optionList.stream()
                .allMatch(option -> option.getStatus() == SaleStatus.SOLD_OUT);
    }

    private void updateFields(UpdateRewardCommand command) {
        updateName(command.name());
        updateDescription(command.description());
        updatePrice(command.price());
        updateStock(command.stockQuantity());
        updatePurchaseLimit(command.purchaseLimitPerPerson());
        updateRewardType(command.rewardType());
    }

    private void updateName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    private void updateDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    private void updatePrice(Integer price) {
        if (price != null) {
            if (price < MINIMUM_AMOUNT) {
                throw new RewardException(PRICE_BELOW_MINIMUM);
            }
            this.price = Money.of(price);
        }
    }

    private void updateStock(Integer stockQuantity) {
        if (stockQuantity != null) {
            this.stock = Stock.of(stockQuantity);
        }
    }

    private void updatePurchaseLimit(Integer purchaseLimitPerPerson) {
        if (purchaseLimitPerPerson != null) {
            this.purchaseLimitPerPerson = purchaseLimitPerPerson;
        }
    }

    private void updateRewardType(RewardType rewardType) {
        if (rewardType != null) {
            this.rewardType = rewardType;
        }
    }

    /**
     * 배송 정책 업데이트
     * - 각 필드가 null이면 기존 값 유지
     */
    private void updateShippingPolicy(UpdateRewardCommand command) {
        if (command.shippingFee() != null || command.freeShippingAmount() != null) {
            Integer newShippingFee = command.shippingFee() != null
                    ? command.shippingFee()
                    : this.shippingPolicy.getShippingFee();

            Integer newFreeShippingAmount = command.freeShippingAmount() != null
                    ? command.freeShippingAmount()
                    : this.shippingPolicy.getFreeShippingAmount();

            this.shippingPolicy = ShippingPolicy.of(newShippingFee, newFreeShippingAmount);
        }
    }

    /**
     * 리워드 생성자
     */
    private Rewards(ProjectId projectId, CreatorId creatorId, String name, String description,
                    Money price, Stock stock, ShippingPolicy shippingPolicy,
                    Integer purchaseLimitPerPerson, RewardType rewardType,
                    SaleStatus status) {
        this.projectId = projectId;
        this.creatorId = creatorId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.shippingPolicy = shippingPolicy;
        this.purchaseLimitPerPerson = purchaseLimitPerPerson;
        this.rewardType = rewardType;
        this.status = status;
    }
}