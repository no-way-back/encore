package com.nowayback.reward.infrastructure.repository.inbox;

import com.nowayback.reward.domain.inbox.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxJpaRepository extends JpaRepository<Inbox, UUID> {
}
