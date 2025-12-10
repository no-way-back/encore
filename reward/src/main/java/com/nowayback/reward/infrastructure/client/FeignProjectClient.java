package com.nowayback.reward.infrastructure.client;

import java.util.UUID;

import com.nowayback.reward.application.external.ProjectClient;
import com.nowayback.reward.infrastructure.client.dto.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignProjectClient implements ProjectClient {

    private final ProjectApiClient apiClient;

    @Override
    public String getProjectTitle(UUID projectId) {
        ProjectResponse response = apiClient.getProject(projectId);
        return response.title();
    }
}