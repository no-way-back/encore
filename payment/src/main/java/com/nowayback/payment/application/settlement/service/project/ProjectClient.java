package com.nowayback.payment.application.settlement.service.project;

import com.nowayback.payment.application.settlement.service.project.dto.ProjectAccountResult;

import java.util.UUID;

public interface ProjectClient {

    ProjectAccountResult getProjectAccountInfo(UUID projectId);
}
