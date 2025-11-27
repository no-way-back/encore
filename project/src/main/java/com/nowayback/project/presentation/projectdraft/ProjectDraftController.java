package com.nowayback.project.presentation.projectdraft;

import com.nowayback.project.application.ProjectDraftService;
import com.nowayback.project.application.command.SaveFundingDraftCommand;
import com.nowayback.project.application.command.SaveSettlementDraftCommand;
import com.nowayback.project.application.command.SaveStoryDraftCommand;
import com.nowayback.project.application.dto.ProjectFundingDraftResult;
import com.nowayback.project.application.dto.ProjectSettlementDraftResult;
import com.nowayback.project.application.dto.ProjectStoryDraftResult;
import com.nowayback.project.presentation.projectdraft.request.SaveProjectFundingDraftRequest;
import com.nowayback.project.presentation.projectdraft.request.SaveProjectSettlementDraft;
import com.nowayback.project.presentation.projectdraft.request.SaveProjectStoryDraftRequest;
import com.nowayback.project.presentation.projectdraft.response.ProjectDraftCreateResponse;
import com.nowayback.project.presentation.projectdraft.response.ProjectFundingDraftResponse;
import com.nowayback.project.presentation.projectdraft.response.ProjectSettlementDraftResponse;
import com.nowayback.project.presentation.projectdraft.response.ProjectStoryDraftResponse;
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

    @PatchMapping("/projects/drafts/{draftId}/story")
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

    @PatchMapping("/projects/drafts/{draftId}/funding")
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

    @PatchMapping("/projects/drafts/{draftId}/settlement")
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
}
