package com.nowayback.project.domain.projectDraft.entity;

import com.nowayback.project.domain.exception.ProjectDomainErrorCode;
import com.nowayback.project.domain.exception.ProjectDomainException;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_settlement_draft")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSettlementDraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "business_number")
    private String businessNumber;

    @Column(name = "account_bank")
    private String accountBank;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder")
    private String accountHolder;


    public static ProjectSettlementDraft create() {
        return new ProjectSettlementDraft();
    }

    public boolean update(
        String businessNumber,
        String accountBank,
        String accountNumber,
        String accountHolder
    ) {
        validateBank(accountBank);
        validateAccountNumber(accountNumber);
        validateAccountHolder(accountHolder);

        this.businessNumber = businessNumber;
        this.accountBank = accountBank;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;

        return isCompleted();
    }

    private void validateBank(String bank) {
        if (bank != null && bank.isBlank()) {
            throw new ProjectDomainException(ProjectDomainErrorCode.INVALID_SETTLEMENT_BANK);
        }
    }

    private void validateAccountNumber(String number) {
        if (number != null && number.isBlank()) {
            throw new ProjectDomainException(
                ProjectDomainErrorCode.INVALID_SETTLEMENT_ACCOUNT_NUMBER);
        }
    }

    private void validateAccountHolder(String holder) {
        if (holder != null && holder.isBlank()) {
            throw new ProjectDomainException(
                ProjectDomainErrorCode.INVALID_SETTLEMENT_ACCOUNT_HOLDER);
        }
    }

    public boolean isCompleted() {
        return accountBank != null
            && accountNumber != null
            && accountHolder != null;
    }

    public void validateForSubmission() {
        List<String> errors = new ArrayList<>();

        if (accountBank == null || accountBank.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_SETTLEMENT_BANK.getMessage());
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_SETTLEMENT_ACCOUNT_NUMBER.getMessage());
        }
        if (accountHolder == null || accountHolder.isBlank()) {
            errors.add(ProjectDomainErrorCode.INVALID_SETTLEMENT_ACCOUNT_HOLDER.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ProjectDomainException(
                ProjectDomainErrorCode.INVALID_SETTLEMENT_DRAFT_SUBMISSION);
        }
    }

}
