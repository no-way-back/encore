package com.nowayback.reward.infrastructure.repository.event;

import com.nowayback.reward.application.event.repository.ProcessedProjectEventRepository;
import com.nowayback.reward.domain.event.ProcessedProjectEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedProjectEventRepositoryImpl implements ProcessedProjectEventRepository {

    private final ProcessedProjectEventJpaRepository jpaRepository;

    @Override
    public ProcessedProjectEvent save(ProcessedProjectEvent event) {
        return jpaRepository.save(event);
    }

    @Override
    public boolean existsByEventId(String eventId) {
        return jpaRepository.existsByEventId(eventId);
    }
}