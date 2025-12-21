package com.nowayback.reward.infrastructure.repository.outbox;

import com.nowayback.reward.domain.outbox.entity.Outbox;
import com.nowayback.reward.domain.outbox.vo.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {
    @Query("""
        SELECT o FROM Outbox o 
        WHERE o.status = 'PENDING' 
        AND o.retryCount < :maxRetries 
        ORDER BY o.createdAt ASC
        """)
    List<Outbox> findRetryableEvents(@Param("maxRetries") int maxRetries);

    @Modifying
    @Query("""
        DELETE FROM Outbox o 
        WHERE o.status = 'PUBLISHED' 
        AND o.publishedAt < :threshold
        """)
    void deletePublishedEventsBefore(@Param("threshold") LocalDateTime threshold);

    List<Outbox> findByStatus(OutboxStatus status);
}
