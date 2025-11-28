package com.nowayback.project.application;

import com.nowayback.project.application.command.SaveFundingDraftCommand;
import com.nowayback.project.application.command.SaveRewardDraftCommand;
import com.nowayback.project.application.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.command.SaveStoryDraftCommand;
import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.dto.ProjectRewardDraftResult;
import com.nowayback.project.application.dto.ProjectSettlementDraftResult;
import com.nowayback.project.application.dto.ProjectStoryDraftResult;
import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import com.nowayback.project.domain.projectDraft.spec.RewardDraftSpec;
import com.nowayback.project.domain.projectDraft.spec.RewardOptionSpec;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectDraftService {

    private final ProjectDraftRepository projectDraftRepository;

    @Transactional
    public UUID createProjectDraft(UUID userId) {
        ProjectDraft projectDraft = ProjectDraft.create(userId);

        return projectDraftRepository.save(projectDraft).getId();
    }

    @Transactional
    public ProjectStoryDraftResult saveStoryDraft(SaveStoryDraftCommand command) {
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateStoryDraft(
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
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateFundingDraft(
            command.goalAmount(),
            command.fundingStartDate(),
            command.fundingEndDate()
        );

        return ProjectFundingDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectSettlementDraftResult saveSettlementDraft(SaveSettlementDraftCommand command) {
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateSettlementDraft(
            command.businessNumber(),
            command.accountBank(),
            command.accountNumber(),
            command.accountHolder()
        );

        return ProjectSettlementDraftResult.of(projectDraft);
    }

    @Transactional
    public ProjectRewardDraftResult saveRewardDraft(SaveRewardDraftCommand command) {
        ProjectDraft projectDraft = findUpdateAbleDraftOrThrow(command.projectDraftId());

        projectDraft.updateRewardDraft(getRewardDraftSpec(command));

        return ProjectRewardDraftResult.of(projectDraft);
    }

    private ProjectDraft findUpdateAbleDraftOrThrow(UUID projectDraftId) {
        return projectDraftRepository.findByIdAndStatus(
            projectDraftId,
            ProjectDraftStatus.DRAFT
        ).orElseThrow(() -> new IllegalArgumentException());
    }

    private List<RewardDraftSpec> getRewardDraftSpec(SaveRewardDraftCommand command) {
        return command.saveRewardCommands().stream()
            .map(spec -> new RewardDraftSpec(
                spec.title(),
                new RewardPrice(
                    spec.price(),
                    spec.shippingFee(),
                    spec.freeShippingAmount()
                ),
                spec.limitCount(),
                spec.purchaseLimitPerPerson(),
                spec.rewardOptionCommands().stream()
                    .map(opt -> new RewardOptionSpec(
                        opt.additionalPrice(),
                        opt.stockQuantity(),
                        opt.displayOrder()
                    ))
                    .toList()
            ))
            .toList();
    }
}
