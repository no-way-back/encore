package com.nowayback.project.presentation.project;

import com.nowayback.project.application.project.ProjectService;
import com.nowayback.project.application.project.dto.CategoryCodes;
import com.nowayback.project.application.project.dto.Cursor;
import com.nowayback.project.application.project.dto.ProjectCard;
import com.nowayback.project.application.project.dto.ProjectListState;
import com.nowayback.project.application.project.dto.ProjectResult;
import com.nowayback.project.application.project.dto.SettlementResult;
import com.nowayback.project.domain.project.vo.ProjectSortType;
import com.nowayback.project.presentation.project.dto.response.ProjectResponse;
import com.nowayback.project.presentation.project.dto.response.SettlementResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectControllerDoc {
    private final ProjectService projectService;

//    @GetMapping("/projects")
//    public ResponseEntity<PageResponse<ProjectResponse>> getAllProjects(
//        @RequestParam(required = false) ProjectStatus status,
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "10") int size
//    ) {
//        Page<ProjectResult> result = projectService.searchProject(status, page, size);
//
//        return ResponseEntity.ok(
//            PageResponse.fromPage(result.map(ProjectResponse::from))
//        );
//    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
        @PathVariable UUID projectId
    ) {
        ProjectResult result = projectService.getProject(projectId);

        return ResponseEntity.ok(ProjectResponse.from(result));
    }

    @GetMapping("/projects/{projectId}/account")
    public ResponseEntity<SettlementResponse> getProjectAccount(
        @PathVariable UUID projectId
    ) {
        SettlementResult result = projectService.getProjectSettlement(projectId);

        return ResponseEntity.ok(SettlementResponse.from(result));
    }

    @GetMapping("/projects")
    public Page<ProjectCard> searchProjects(
        @RequestParam(required = false) String rootCategoryCode,
        @RequestParam(required = false) String categoryCode,
        @RequestParam(required = false) ProjectSortType sortType,
        @RequestParam(required = false) ProjectListState state,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return projectService.searchProjects(
            new Cursor(sortType, page, size),
            new CategoryCodes(rootCategoryCode, categoryCode),
            state
        );
    }
}
