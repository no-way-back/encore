package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.projectDraft.spec.RewardOptionSpec;
import com.nowayback.project.domain.projectDraft.vo.RewardOptions;
import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import com.nowayback.project.domain.projectDraft.vo.RewardType;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_reward_drafts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRewardDraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_draft_id", nullable = false)
    private ProjectDraft projectDraft;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RewardType rewardType;

    @Embedded
    private RewardOptions rewardOptions = new RewardOptions();

    private String title;
    private RewardPrice rewardPrice;
    private Integer limitCount;

    private Integer purchaseLimitPerPerson;


    public static ProjectRewardDraft create() {
        return new ProjectRewardDraft();
    }
    public boolean update(
        String title,
        RewardType rewardType,
        RewardPrice price,
        Integer limitCount,
        Integer purchaseLimitPerPerson,
        List<RewardOptionSpec> optionSpecs
    ) {
        validateLimit(limitCount);

        this.title = title;
        this.rewardPrice = price;
        this.limitCount = limitCount;
        this.rewardType = rewardType;
        this.purchaseLimitPerPerson = purchaseLimitPerPerson;

        if (!optionSpecs.isEmpty()) {
            replaceOptions(optionSpecs);
        }

        return isCompleted();
    }

    private void replaceOptions(List<RewardOptionSpec> optionSpecs) {
        this.rewardOptions.clear();

        optionSpecs.forEach(spec -> {
            ProjectRewardOptionDraft option = ProjectRewardOptionDraft.create();
            option.update(
                spec.name(),
                spec.isRequired(),
                spec.additionalPrice(),
                spec.stockQuantity(),
                spec.displayOrder()
            );
            this.rewardOptions.add(option);
        });
    }

    private void validateLimit(Integer limitCount) {
        if (limitCount != null && limitCount < 0) {
            throw new ProjectException(ProjectErrorCode.INVALID_REWARD_LIMIT);
        }
    }

    public boolean isCompleted() {
        return title != null
            && rewardPrice != null
            && limitCount != null
            && rewardType != null;
    }

    public void validateForSubmission() {
        List<String> errors = new ArrayList<>();

        if (title == null || title.isBlank()) {
            errors.add(ProjectErrorCode.INVALID_REWARD_PRICE.getMessage());
        }

        if (limitCount == null || limitCount < 0) {
            errors.add(ProjectErrorCode.INVALID_REWARD_LIMIT.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ProjectException(
                ProjectErrorCode.INVALID_REWARD_DRAFT_SUBMISSION);
        }
    }

    public void assignTo(ProjectDraft projectDraft) {
        this.projectDraft = projectDraft;
    }
}
