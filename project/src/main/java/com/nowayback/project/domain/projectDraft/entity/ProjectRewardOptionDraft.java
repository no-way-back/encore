package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_project_reward_option_drafts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRewardOptionDraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "additional_price")
    private Integer additionalPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "display_order")
    private Integer displayOrder;


    public static ProjectRewardOptionDraft create() {
        return new ProjectRewardOptionDraft();
    }

    public boolean update(
        Integer additionalPrice,
        Integer stockQuantity,
        Integer displayOrder
    ) {
        validateAdditionalPrice(additionalPrice);
        validateStock(stockQuantity);
        validateDisplayOrder(displayOrder);

        this.additionalPrice = additionalPrice;
        this.stockQuantity = stockQuantity;
        this.displayOrder = displayOrder;

        return isCompleted();
    }

    private void validateAdditionalPrice(Integer additionalPrice) {
        if (additionalPrice != null && additionalPrice < 0) {
            throw new ProjectException(ProjectErrorCode.INVALID_REWARD_OPTION_PRICE);
        }
    }

    private void validateStock(Integer stockQuantity) {
        if (stockQuantity != null && stockQuantity < 0) {
            throw new ProjectException(ProjectErrorCode.INVALID_REWARD_OPTION_STOCK);
        }
    }

    private void validateDisplayOrder(Integer displayOrder) {
        if (displayOrder != null && displayOrder < 0) {
            throw new ProjectException(
                ProjectErrorCode.INVALID_REWARD_OPTION_DISPLAY_ORDER);
        }
    }

    private boolean isCompleted() {
        return additionalPrice != null
            && stockQuantity != null
            && displayOrder != null;
    }


    public void validateForSubmission() {
        List<String> errors = new ArrayList<>();

        if (additionalPrice == null || additionalPrice < 0) {
            errors.add(ProjectErrorCode.INVALID_REWARD_OPTION_PRICE.getMessage());
        }
        if (stockQuantity == null || stockQuantity < 0) {
            errors.add(ProjectErrorCode.INVALID_REWARD_OPTION_STOCK.getMessage());
        }
        if (displayOrder == null || displayOrder < 0) {
            errors.add(ProjectErrorCode.INVALID_REWARD_OPTION_DISPLAY_ORDER.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ProjectException(
                ProjectErrorCode.INVALID_REWARD_OPTION_DRAFT_SUBMISSION);
        }
    }

}
