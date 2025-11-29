package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_drafts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectDraftStatus status;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "story_draft_id")
    private ProjectStoryDraft storyDraft;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_draft_id")
    private List<ProjectRewardDraft> rewardDrafts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "funding_draft_id")
    private ProjectFundingDraft fundingDraft;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "settlement_draft_id")
    private ProjectSettlementDraft settlementDraft;

    private ProjectDraft(UUID userId) {
        this.userId = userId;
        this.status = ProjectDraftStatus.DRAFT;
    }

    public static ProjectDraft create(UUID userId) {
        return new ProjectDraft(userId);
    }

    public void submit() {
        if (!isAllStepsCompleted()) {
            throw new ProjectException(ProjectErrorCode.INVALID_DRAFT_SUBMISSION);
        }
        this.status = ProjectDraftStatus.SUBMITTED;
    }

    private boolean isAllStepsCompleted() {
        return storyDraft != null && storyDraft.isCompleted()
            && fundingDraft != null && fundingDraft.isCompleted()
            && settlementDraft != null && settlementDraft.isCompleted()
            && rewardDrafts.stream().allMatch(ProjectRewardDraft::isCompleted);
    }


    public void assignStoryDraft(ProjectStoryDraft storyDraft) {
        this.storyDraft = storyDraft;
    }

    public void assignFundingDraft(ProjectFundingDraft fundingDraft) {
        this.fundingDraft = fundingDraft;
    }

    public void assignSettlementDraft(ProjectSettlementDraft settlementDraft) {
        this.settlementDraft = settlementDraft;
    }

    public void replaceRewardDrafts(List<ProjectRewardDraft> drafts) {
        this.rewardDrafts.clear();
        this.rewardDrafts.addAll(drafts);
    }

    public void ensureUpdatable() {
        if (this.status != ProjectDraftStatus.DRAFT) {
            throw new ProjectException(ProjectErrorCode.INVALID_PROJECT_DRAFT_STATUS);
        }
    }
}
