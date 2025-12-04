package com.nowayback.payment.infrastructure.settlement.external.project;

import com.nowayback.payment.infrastructure.settlement.external.project.dto.ProjectAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "project-service")
public interface ProjectFeignClient {

    @GetMapping("/projects/{projectId}/account")
    ProjectAccountResponse getProjectAccountInfo(@PathVariable UUID projectId);
}
