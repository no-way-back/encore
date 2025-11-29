package com.nowayback.reward.domain.reward.entity;

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

@Entity
@Table(name = "p_rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rewards extends BaseEntity {

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
        return new Rewards(
                ProjectId.of(command.projectId()),
                CreatorId.of(command.creatorId()),  // 👈 VO 변환
                command.name(),
                command.description(),
                Money.of(command.price()),
                Stock.of(command.stockQuantity()),
                ShippingPolicy.of(command.shippingFee(), command.freeShippingAmount()),
                command.purchaseLimitPerPerson(),
                command.rewardType(),
                SaleStatus.AVAILABLE
        );
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
            throw new RewardException(RewardErrorCode.DUPLICATE_OPTION_NAME);
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
            throw new RewardException(RewardErrorCode.DUPLICATE_DISPLAY_ORDER);
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