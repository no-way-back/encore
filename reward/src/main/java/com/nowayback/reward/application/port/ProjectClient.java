package com.nowayback.reward.application.port;

import java.util.UUID;

public interface ProjectClient {
    String getProjectTitle(UUID projectId);
}