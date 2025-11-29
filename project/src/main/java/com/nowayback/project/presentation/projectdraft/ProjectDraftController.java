package com.nowayback.project.presentation.projectdraft;

import com.nowayback.project.application.ProjectDraftService;
import com.nowayback.project.application.command.SaveFundingDraftCommand;
import com.nowayback.project.application.command.SaveRewardDraftCommand;
import com.nowayback.project.application.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.command.SaveStoryDraftCommand;
import com.nowayback.project.application.dto.ProjectDraftResult;
import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.dto.ProjectRewardDraftResult;
import com.nowayback.project.application.dto.ProjectSettlementDraftResult;
import com.nowayback.project.application.dto.ProjectStoryDraftResult;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveProjectFundingDraftRequest;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveProjectSettlementDraft;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveProjectStoryDraftRequest;
import com.nowayback.project.presentation.projectdraft.dto.request.SaveRewardDraftRequest;
import com.nowayback.project.presentation.projectdraft.dto.response.PageResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectDraftCreateResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectFundingDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectRewardDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectSettlementDraftResponse;
import com.nowayback.project.presentation.projectdraft.dto.response.ProjectStoryDraftResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PatchMapping("/project-drafts/{draftId}/stories")
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

    @GetMapping("/project-drafts/{projectDraftId}/fundings")
    public ResponseEntity<ProjectFundingDraftResponse> getFundingDraft(
        @PathVariable UUID projectDraftId
    ) {
        ProjectFundingDraftResult result = projectDraftService.getFundingDraft(projectDraftId);

        return ResponseEntity.ok(ProjectFundingDraftResponse.from(result));
    }

    @GetMapping("/project-drafts/{projectDraftId}/rewards")
    public ResponseEntity<ProjectRewardDraftResponse> getRewardDraft(
        @PathVariable UUID projectDraftId
    ) {
        ProjectRewardDraftResult result = projectDraftService.getRewardDraft(projectDraftId);
        return ResponseEntity.ok(ProjectRewardDraftResponse.from(result));
    }

    @GetMapping("/project-drafts/{projectDraftId}/stories")
    public ResponseEntity<ProjectStoryDraftResponse> getStoryDraft(
        @PathVariable UUID projectDraftId
    ) {
        ProjectStoryDraftResult result = projectDraftService.getStoryDraft(projectDraftId);
        return ResponseEntity.ok(ProjectStoryDraftResponse.from(result));
    }

    @GetMapping("/project-drafts/{projectDraftId}/settlements")
    public ResponseEntity<ProjectSettlementDraftResponse> getSettlementDraft(
        @PathVariable UUID projectDraftId
    ) {
        ProjectSettlementDraftResult result = projectDraftService.getSettlementDraft(projectDraftId);
        return ResponseEntity.ok(ProjectSettlementDraftResponse.from(result));
    }

    @GetMapping("/project-drafts/me")
    public ResponseEntity<PageResponse<ProjectDraftResponse>> getDrafts(
        @RequestParam(required = false) ProjectDraftStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        // TODO: 삭제 필요
        UUID userId = UUID.fromString("23529dc5-e8cf-4325-bc4f-241f529244ed");

        Page<ProjectDraftResult> result = projectDraftService.searchDrafts(
            userId,
            status,
            page,
            size
        );

        return ResponseEntity.ok(
            PageResponse.fromPage(result.map(ProjectDraftResponse::of))
        );
    }

    @PostMapping("/project-drafts/{projectDraftId}/submit")
    public ResponseEntity<Void> submitDraft(
        @PathVariable UUID projectDraftId
    ) {
        projectDraftService.submit(projectDraftId);

        return ResponseEntity.noContent().build();
    }
}
