package com.nowayback.project.domain.outbox.repository;

import com.nowayback.project.domain.outbox.Outbox;
import java.util.List;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    List<Outbox> findPendingEvents();
}
