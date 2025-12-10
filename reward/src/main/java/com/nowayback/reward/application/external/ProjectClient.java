package com.nowayback.reward.application.external;

import java.util.UUID;

public interface ProjectClient {
    String getProjectTitle(UUID projectId);
}