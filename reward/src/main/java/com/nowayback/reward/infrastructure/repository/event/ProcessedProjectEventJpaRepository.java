package com.nowayback.reward.infrastructure.repository.event;

import com.nowayback.reward.domain.event.ProcessedProjectEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedProjectEventJpaRepository extends JpaRepository<ProcessedProjectEvent, String> {
    boolean existsByEventId(String eventId);
}