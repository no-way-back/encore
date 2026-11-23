package com.nowayback.reward.infrastructure.repository.inbox;

import com.nowayback.reward.domain.inbox.entity.Inbox;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface InboxJpaRepository extends JpaRepository<Inbox, UUID> {

    /**
     * event_id가 미리 정해진 엔티티라 save()는 persist가 아닌 merge로 동작해,
     * 중복 이벤트가 와도 예외 없이 기존 행을 덮어쓴다.
     * 그래서 INSERT ... ON CONFLICT DO NOTHING으로 "처음 받은 경우에만 저장"을 DB가 보장하게 한다.
     */
    @Modifying
    @Query(value = """
            INSERT INTO p_inbox (event_id, event_type, aggregate_id, status, payload, received_at, retry_count)
            VALUES (:eventId, :eventType, :aggregateId, 'RECEIVED', :payload, :receivedAt, 0)
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("eventId") UUID eventId,
            @Param("eventType") String eventType,
            @Param("aggregateId") UUID aggregateId,
            @Param("payload") String payload,
            @Param("receivedAt") LocalDateTime receivedAt
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inbox i WHERE i.id = :id")
    Optional<Inbox> findByIdForUpdate(@Param("id") UUID id);
}
