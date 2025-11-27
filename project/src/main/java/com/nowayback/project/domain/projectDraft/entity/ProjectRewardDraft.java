package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectDomainErrorCode;
import com.nowayback.project.domain.exception.ProjectDomainException;
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

@Getter
@Entity
@Table(name = "p_project_reward_draft")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRewardDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private Long price;
    private Integer limitCount;
    private Integer shippingFee;
    private Integer freeShippingAmount;
    private Integer purchaseLimitPerPerson;


    public static ProjectRewardDraft create() {
        return new ProjectRewardDraft();
    }

    public boolean update(
        String title,
        Long price,
        Integer limitCount,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson
    ) {
        validatePrice(price);
        validateLimit(limitCount);
        validateShippingFee(shippingFee);

        this.title = title;
        this.price = price;
        this.limitCount = limitCount;
        this.shippingFee = shippingFee;
        this.freeShippingAmount = freeShippingAmount;
        this.purchaseLimitPerPerson = purchaseLimitPerPerson;

        return isCompleted();
    }

    private void validatePrice(Long price) {
        if (price != null && price <= 0) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_REWARD_PRICE);
        }
    }

    private void validateLimit(Integer limitCount) {
        if (limitCount != null && limitCount < 0) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_REWARD_LIMIT);
        }
    }

    private void validateShippingFee(Integer shippingFee) {
        if (shippingFee != null && shippingFee < 0) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_REWARD_SHIPPING_FEE);
        }
    }

    public boolean isCompleted() {
        return title != null
            && price != null
            && limitCount != null
            && shippingFee != null;
    }

    public void validateForSubmission() {
        List<String> errors = new ArrayList<>();

        if (title == null || title.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_REWARD_PRICE.getMessage());
        }
        if (price == null || price <= 0) {
            errors.add(ProjectDomainErrorCode.INVALID_REWARD_PRICE.getMessage());
        }
        if (limitCount == null || limitCount < 0) {
            errors.add(ProjectDomainErrorCode.INVALID_REWARD_LIMIT.getMessage());
        }
        if (shippingFee == null || shippingFee < 0) {
            errors.add(ProjectDomainErrorCode.INVALID_REWARD_SHIPPING_FEE.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ProjectDomainException(
                ProjectDomainErrorCode.INVALID_REWARD_DRAFT_SUBMISSION);
        }
    }

}
