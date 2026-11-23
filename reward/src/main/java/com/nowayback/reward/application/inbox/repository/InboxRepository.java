package com.nowayback.reward.application.inbox.repository;

import com.nowayback.reward.domain.inbox.entity.Inbox;

import java.util.Optional;
import java.util.UUID;

public interface InboxRepository {
    Inbox save(Inbox inbox);

    Optional<Inbox> findById(UUID id);

    /**
     * 이벤트를 처음 받은 경우에만 저장한다.
     * 같은 event_id가 이미 있으면 아무것도 쓰지 않고 false를 돌려준다.
     */
    boolean saveIfAbsent(Inbox inbox);

    /**
     * 같은 이벤트가 동시에 처리되지 않도록 행 잠금을 걸고 조회한다.
     */
    Optional<Inbox> findByIdForUpdate(UUID id);
}
