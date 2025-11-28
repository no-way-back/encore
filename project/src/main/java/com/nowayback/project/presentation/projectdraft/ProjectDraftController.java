package com.nowayback.project.presentation.projectdraft;

import com.nowayback.project.application.ProjectDraftService;
import com.nowayback.project.application.command.SaveFundingDraftCommand;
import com.nowayback.project.application.command.SaveRewardDraftCommand;
import com.nowayback.project.application.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.command.SaveStoryDraftCommand;
import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.dto.ProjectSettlementDraftResult;
import com.nowayback.project.application.dto.ProjectStoryDraftResult;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveProjectFundingDraftRequest;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveProjectSettlementDraft;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveProjectStoryDraftRequest;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveRewardDraftRequest;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectDraftCreateResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectFundingDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectRewardDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectSettlementDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectStoryDraftResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectDraftController {

    private final ProjectDraftService projectDraftService;

    @PostMapping("/projects/drafts")
    public ResponseEntity<ProjectDraftCreateResponse> createProjectDraft() {
        //TODO
        UUID userId = UUID.randomUUID();

        UUID projectDraftId = projectDraftService.createProjectDraft(userId);

        return ResponseEntity.ok(ProjectDraftCreateResponse.of(projectDraftId));
    }

    @PatchMapping("/projects/drafts/{draftId}/stores")
    public ResponseEntity<ProjectStoryDraftResponse> saveProjectStoryDraft(
        @PathVariable("draftId") UUID draftId,
        @RequestBody SaveProjectStoryDraftRequest request
    ) {
        //TODO
        UUID userId = UUID.randomUUID();

        ProjectStoryDraftResult result = projectDraftService.saveStoryDraft(
            SaveStoryDraftCommand.of(
                draftId,
                userId,
                request.title(),
                request.summary(),
                request.category(),
                request.thumbnailUrl(),
                request.contentJson()
            )
        );

        return ResponseEntity.ok(ProjectStoryDraftResponse.from(result));
    }

    @PatchMapping("/projects/drafts/{draftId}/fundings")
    public ResponseEntity<ProjectFundingDraftResponse> saveProjectFundingDraft(
        @PathVariable("draftId") UUID draftId,
        @RequestBody SaveProjectFundingDraftRequest request
    ) {
        //TODO
        UUID userId = UUID.randomUUID();

        ProjectFundingDraftResult result = projectDraftService.saveFundingDraft(
            SaveFundingDraftCommand.of(
                draftId,
                request.goalAmount(),
                request.fundingStartDate(),
                request.fundingEndDate()
            )
        );

        return ResponseEntity.ok(ProjectFundingDraftResponse.from(result));
    }

    @PatchMapping("/projects/drafts/{draftId}/settlements")
    public ResponseEntity<ProjectSettlementDraftResponse> saveProjectSettlementDraft(
        @PathVariable("draftId") UUID draftId,
        @RequestBody SaveProjectSettlementDraft request
    ) {
        //TODO
        UUID userId = UUID.randomUUID();

        ProjectSettlementDraftResult result = projectDraftService.saveSettlementDraft(
            SaveSettlementDraftCommand.of(
                draftId,
                request.businessNumber(),
                request.accountBank(),
                request.accountNumber(),
                request.accountHolder()
            )
        );

        return ResponseEntity.ok(ProjectSettlementDraftResponse.from(result));
    }

    @PatchMapping("/projects/drafts/{projectDraftId}/rewards")
    public ResponseEntity<ProjectRewardDraftResponse> saveRewardDraft(
        @PathVariable UUID projectDraftId,
        @RequestBody SaveRewardDraftRequest request
    ) {

        SaveRewardDraftCommand command =
            SaveRewardDraftCommand.of(
                projectDraftId,
                request.rewards().stream()
                    .map(r -> SaveRewardDraftCommand.RewardDraftCommand.of(
                        r.title(),
                        r.price(),
                        r.limitCount(),
                        r.shippingFee(),
                        r.freeShippingAmount(),
                        r.purchaseLimitPerPerson(),
                        r.options() == null
                            ? List.of()
                            : r.options().stream()
                                .map(o -> SaveRewardDraftCommand.RewardOptionCommand.of(
                                    o.additionalPrice(),
                                    o.stockQuantity(),
                                    o.displayOrder()
                                ))
                                .toList()
                    ))
                    .toList()
            );

        var result = projectDraftService.saveRewardDraft(command);

        return ResponseEntity.ok(ProjectRewardDraftResponse.from(result));
    }
}
