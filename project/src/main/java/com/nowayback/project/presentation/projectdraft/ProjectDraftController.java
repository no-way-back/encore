package com.nowayback.project.presentation.projectdraft;

import com.nowayback.project.application.ProjectDraftService;
import com.nowayback.project.application.command.SaveFundingDraftCommand;
import com.nowayback.project.application.command.SaveRewardDraftCommand;
import com.nowayback.project.application.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.command.SaveStoryDraftCommand;
import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.dto.ProjectRewardDraftResult;
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

    @PostMapping("/project-drafts")
    public ResponseEntity<ProjectDraftCreateResponse> createProjectDraft() {
        //TODO
        UUID userId = UUID.randomUUID();

        UUID projectDraftId = projectDraftService.createProjectDraft(userId);

        return ResponseEntity.ok(ProjectDraftCreateResponse.of(projectDraftId));
    }

    @PatchMapping("/project-drafts/{draftId}/stores")
    public ResponseEntity<ProjectStoryDraftResponse> saveProjectStoryDraft(
        @PathVariable("draftId") UUID draftId,
        @RequestBody SaveProjectStoryDraftRequest request
    ) {
        //TODO
        UUID userId = UUID.randomUUID();

        SaveStoryDraftCommand command = request.toCommand(draftId, userId);

        ProjectStoryDraftResult result = projectDraftService.saveStoryDraft(command);

        return ResponseEntity.ok(ProjectStoryDraftResponse.from(result));
    }

    @PatchMapping("/project-drafts/{draftId}/fundings")
    public ResponseEntity<ProjectFundingDraftResponse> saveProjectFundingDraft(
        @PathVariable("draftId") UUID draftId,
        @RequestBody SaveProjectFundingDraftRequest request
    ) {
        //TODO
        UUID userId = UUID.randomUUID();

        SaveFundingDraftCommand command = request.toCommand(draftId, userId);

        ProjectFundingDraftResult result = projectDraftService.saveFundingDraft(command);

        return ResponseEntity.ok(ProjectFundingDraftResponse.from(result));
    }

    @PatchMapping("/project-drafts/{draftId}/settlements")
    public ResponseEntity<ProjectSettlementDraftResponse> saveProjectSettlementDraft(
        @PathVariable("draftId") UUID draftId,
        @RequestBody SaveProjectSettlementDraft request
    ) {
        //TODO
        UUID userId = UUID.randomUUID();

        SaveSettlementDraftCommand command = request.toCommand(draftId, userId);

        ProjectSettlementDraftResult result = projectDraftService.saveSettlementDraft(command);

        return ResponseEntity.ok(ProjectSettlementDraftResponse.from(result));
    }

    @PatchMapping("/project-drafts/{projectDraftId}/rewards")
    public ResponseEntity<ProjectRewardDraftResponse> saveRewardDraft(
        @PathVariable UUID projectDraftId,
        @RequestBody SaveRewardDraftRequest request
    ) {
        UUID userId = UUID.randomUUID();

        SaveRewardDraftCommand command = request.toCommand(projectDraftId, userId);

        ProjectRewardDraftResult result = projectDraftService.saveRewardDraft(command);

        return ResponseEntity.ok(ProjectRewardDraftResponse.from(result));
    }
}
