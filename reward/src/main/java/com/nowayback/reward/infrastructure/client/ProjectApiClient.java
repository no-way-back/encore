package com.nowayback.reward.infrastructure.client;

import com.nowayback.reward.infrastructure.client.dto.ProjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "project-service",
        url = "${service.project.url}"
)
public interface ProjectApiClient {
    @GetMapping("/projects/{projectId}")
    ProjectResponse getProject(@PathVariable UUID projectId);
}