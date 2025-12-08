package com.nowayback.payment.domain.jobs.entity;

import com.nowayback.payment.domain.jobs.vo.RefundJobStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_refund_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefundJob {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "payment_id", nullable = false, unique = true)
    private UUID paymentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RefundJobStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "reason")
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private RefundJob(UUID paymentId, UUID projectId, String reason) {
        this.paymentId = paymentId;
        this.projectId = projectId;
        this.status = RefundJobStatus.PENDING;
        this.retryCount = 0;
        this.reason = reason;
    }

    /* Factory Method */

    public static RefundJob create(UUID paymentId, UUID projectId, String reason) {
        return new RefundJob(paymentId, projectId, reason);
    }

    /* Business Methods */

    public void markInProgress() {
        this.status = RefundJobStatus.IN_PROGRESS;
    }

    public void markCompleted() {
        this.status = RefundJobStatus.COMPLETED;
    }

    public void markFailed() {
        this.status = RefundJobStatus.FAILED;
        this.retryCount += 1;
    }
}
