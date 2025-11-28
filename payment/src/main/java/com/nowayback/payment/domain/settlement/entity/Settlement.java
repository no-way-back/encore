package com.nowayback.payment.domain.settlement.entity;

import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import com.nowayback.payment.domain.settlement.vo.*;
import com.nowayback.payment.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "project_id", updatable = false, nullable = false))
    private ProjectId projectId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total_amount", updatable = false, nullable = false))
    private Money totalAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "service_fee", updatable = false, nullable = false))
    private Money serviceFee;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "pg_fee", updatable = false, nullable = false))
    private Money pgFee;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "net_amount", updatable = false, nullable = false))
    private Money netAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "accountBank", column = @Column(name = "account_bank", updatable = false, nullable = false, length = 20)),
            @AttributeOverride(name = "accountNumber", column = @Column(name = "account_number", updatable = false, nullable = false, length = 50)),
            @AttributeOverride(name = "accountHolderName", column = @Column(name = "account_holder_name", updatable = false, nullable = false, length = 20))
    })
    private AccountInfo accountInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementStatus status;

    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;

    private LocalDateTime failedAt;

    private Settlement(ProjectId projectId, Money totalAmount, Money serviceFee, Money pgFee, Money netAmount, AccountInfo accountInfo) {
        this.projectId = projectId;
        this.totalAmount = totalAmount;
        this.serviceFee = serviceFee;
        this.pgFee = pgFee;
        this.netAmount = netAmount;
        this.accountInfo = accountInfo;
        this.status = SettlementStatus.PROCESSING;
        this.requestedAt = LocalDateTime.now();
    }

    /* Factory Method */

    public static Settlement create(ProjectId projectId, Money totalAmount, AccountInfo accountInfo, SettlementFeePolicy feePolicy) {
        validateSettlement(projectId, totalAmount, accountInfo);

        Money serviceFee = feePolicy.calculateServiceFee(totalAmount);
        Money pgFee = feePolicy.calculatePgFee(totalAmount);
        Money netAmount = feePolicy.calculateNetAmount(totalAmount, serviceFee, pgFee);

        return new Settlement(projectId, totalAmount, serviceFee, pgFee, netAmount, accountInfo);
    }

    /* Business Method */

    public void complete() {
        this.status = SettlementStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = SettlementStatus.FAILED;
        this.failedAt = LocalDateTime.now();
    }

    /* Validation Methods */

    private static void validateSettlement(ProjectId projectId, Money totalAmount, AccountInfo accountInfo) {
        validateProjectId(projectId);
        validateTotalAmount(totalAmount);
        validateAccountInfo(accountInfo);
    }

    private static void validateProjectId(ProjectId projectId) {
        if (projectId == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_PROJECT_ID_OBJECT);
        }
    }

    private static void validateTotalAmount(Money totalAmount) {
        if (totalAmount == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_SETTLEMENT_TOTAL_AMOUNT_OBJECT);
        }
    }

    private static void validateAccountInfo(AccountInfo accountInfo) {
        if (accountInfo == null) {
            throw new PaymentDomainException(PaymentDomainErrorCode.NULL_SETTLEMENT_ACCOUNT_INFO_OBJECT);
        }
    }
}