package com.nowayback.reward.application.event.repository;

import com.nowayback.reward.domain.event.ProcessedProjectEvent;

public interface ProcessedProjectEventRepository {
    ProcessedProjectEvent save(ProcessedProjectEvent event);
    boolean existsByEventId(String eventId);
}