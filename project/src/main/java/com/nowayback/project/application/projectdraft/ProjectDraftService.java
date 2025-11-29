package com.nowayback.project.application.projectdraft;

import com.nowayback.project.application.project.ProjectService;
import com.nowayback.project.application.project.command.CreateProjectCommand;
import com.nowayback.project.application.projectdraft.command.SaveFundingDraftCommand;
import com.nowayback.project.application.projectdraft.command.SaveRewardDraftCommand;
import com.nowayback.project.application.projectdraft.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.projectdraft.command.SaveStoryDraftCommand;
import com.nowayback.project.application.projectdraft.dto.ProjectDraftResult;
import com.nowayback.project.application.projectdraft.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.projectdraft.dto.ProjectRewardDraftResult;
import com.nowayback.project.application.projectdraft.dto.ProjectSettlementDraftResult;
import com.nowayback.project.application.projectdraft.dto.ProjectStoryDraftResult;
import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectFundingDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectSettlementDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectStoryDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import com.nowayback.project.domain.projectDraft.spec.RewardOptionSpec;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectDraftService {

    private final ProjectService projectService;
    private final ProjectDraftRepository projectDraftRepository;

    @Transactional
    public UUID createProjectDraft(UUID userId) {
        ProjectDraft projectDraft = ProjectDraft.create(userId);
        return projectDraftRepository.save(projectDraft).getId();
    }

    @Transactional
    public ProjectStoryDraftResult saveStoryDraft(SaveStoryDraftCommand command) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(command.projectDraftId());

        projectDraft.ensureUpdatable();

        ProjectStoryDraft story = projectDraft.getStoryDraft();
        if (story == null) {
            story = ProjectStoryDraft.create();
            projectDraft.assignStoryDraft(story);
        }

        story.update(
            command.title(),
            command.summary(),
            command.category(),
            command.thumbnailUrl(),
            command.contentJson()
        );

        return ProjectStoryDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectFundingDraftResult saveFundingDraft(SaveFundingDraftCommand command) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(command.projectDraftId());

        projectDraft.ensureUpdatable();

        ProjectFundingDraft funding = projectDraft.getFundingDraft();
        if (funding == null) {
            funding = ProjectFundingDraft.create();
            projectDraft.assignFundingDraft(funding);
        }

        funding.update(
            command.goalAmount(),
            command.fundingStartDate(),
            command.fundingEndDate()
        );

        return ProjectFundingDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectSettlementDraftResult saveSettlementDraft(SaveSettlementDraftCommand command) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(command.projectDraftId());

        projectDraft.ensureUpdatable();

        ProjectSettlementDraft settlement = projectDraft.getSettlementDraft();
        if (settlement == null) {
            settlement = ProjectSettlementDraft.create();
            projectDraft.assignSettlementDraft(settlement);
        }

        settlement.update(
            command.businessNumber(),
            command.accountBank(),
            command.accountNumber(),
            command.accountHolder()
        );

        return ProjectSettlementDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectRewardDraftResult saveRewardDraft(SaveRewardDraftCommand command) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(command.projectDraftId());

        projectDraft.ensureUpdatable();

        List<ProjectRewardDraft> drafts = command.saveRewardCommands().stream()
            .map(spec -> toRewardDraft(spec))
            .toList();

        projectDraft.replaceRewardDrafts(drafts);

        return ProjectRewardDraftResult.of(projectDraft);
    }


    public ProjectFundingDraftResult getFundingDraft(UUID projectDraftId) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(projectDraftId);
        if (projectDraft.getFundingDraft() == null) {
            throw new ProjectException(ProjectErrorCode.FUNDING_DRAFT_NOT_FOUND);
        }
        return ProjectFundingDraftResult.of(projectDraft);
    }

    public ProjectRewardDraftResult getRewardDraft(UUID projectDraftId) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(projectDraftId);
        if (projectDraft.getRewardDrafts() == null || projectDraft.getRewardDrafts().isEmpty()) {
            throw new ProjectException(ProjectErrorCode.REWARD_DRAFT_NOT_FOUND);
        }
        return ProjectRewardDraftResult.of(projectDraft);
    }

    public ProjectSettlementDraftResult getSettlementDraft(UUID projectDraftId) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(projectDraftId);
        if (projectDraft.getSettlementDraft() == null) {
            throw new ProjectException(ProjectErrorCode.SETTLEMENT_DRAFT_NOT_FOUND);
        }
        return ProjectSettlementDraftResult.of(projectDraft);
    }

    public ProjectStoryDraftResult getStoryDraft(UUID projectDraftId) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(projectDraftId);
        if (projectDraft.getStoryDraft() == null) {
            throw new ProjectException(ProjectErrorCode.STORY_DRAFT_NOT_FOUND);
        }
        return ProjectStoryDraftResult.of(projectDraft);
    }

    public Page<ProjectDraftResult> searchDrafts(
        UUID userId,
        ProjectDraftStatus status,
        int page,
        int size
    ) {
        Page<ProjectDraft> projectDrafts = projectDraftRepository.searchDrafts(
            userId,
            status,
            page,
            size
        );
        return projectDrafts.map(ProjectDraftResult::of);
    }

    public void submit(UUID projectDraftId) {
        ProjectDraft projectDraft = findProjectDraftOrThrow(projectDraftId);
        projectDraft.ensureUpdatable();

        projectDraft.submit();

        projectService.createProject(
            CreateProjectCommand.of(
                projectDraft.getUserId(),
                projectDraft.getStoryDraft().getTitle(),
                projectDraft.getStoryDraft().getSummary(),
                projectDraft.getStoryDraft().getCategory(),
                projectDraft.getStoryDraft().getThumbnailUrl(),
                projectDraft.getStoryDraft().getContentJson(),
                projectDraft.getFundingDraft().getGoalAmount(),
                projectDraft.getFundingDraft().getFundingStartDate(),
                projectDraft.getFundingDraft().getFundingEndDate()
            )
        );
    }

    private ProjectDraft findProjectDraftOrThrow(UUID projectDraftId) {
        return projectDraftRepository.findById(projectDraftId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_DRAFT_NOT_FOUND));
    }

    private ProjectRewardDraft toRewardDraft(SaveRewardDraftCommand.RewardDraftCommand spec) {
        ProjectRewardDraft draft = ProjectRewardDraft.create();

        draft.update(
            spec.title(),
            new RewardPrice(spec.price(), spec.shippingFee(), spec.freeShippingAmount()),
            spec.limitCount(),
            spec.purchaseLimitPerPerson(),
            spec.rewardOptionCommands().stream()
                .map(o -> new RewardOptionSpec(
                    o.additionalPrice(),
                    o.stockQuantity(),
                    o.displayOrder()
                ))
                .toList()
        );

        return draft;
    }
}
