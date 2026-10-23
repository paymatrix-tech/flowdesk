package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.flowdesk.api.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PaymentReversalRecord implements Serializable{

    private Long id;

    private Long achReversalId;

    private Long returnId;

    private Payment payment;

    private LocalDate reversalDate;

    private LocalDate reversalExpiryDate;

    private String status;

    private LocalDate memoPostDate;

    private MemoPostStatus memoPostStatus;

    private AccountingStatus customerAccountingStatus;

    private LocalDate customerAccountingDate;

    private AccountingStatus glAccountingStatus;

    private LocalDate glAccountingDate;

    private String fedSettlementId;

    private String transmissionStatus;

    private String createdBy;

    private String initiatedBy;

    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    private LocalDateTime createdDateTime;

    private String lastUpdatedBy;

    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    private LocalDateTime lastUpdatedDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAchReversalId() {
        return achReversalId;
    }

    public void setAchReversalId(Long achReversalId) {
        this.achReversalId = achReversalId;
    }

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public LocalDate getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(LocalDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    public LocalDate getReversalExpiryDate() {
        return reversalExpiryDate;
    }

    public void setReversalExpiryDate(LocalDate reversalExpiryDate) {
        this.reversalExpiryDate = reversalExpiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getMemoPostDate() {
        return memoPostDate;
    }

    public void setMemoPostDate(LocalDate memoPostDate) {
        this.memoPostDate = memoPostDate;
    }

    public MemoPostStatus getMemoPostStatus() {
        return memoPostStatus;
    }

    public void setMemoPostStatus(MemoPostStatus memoPostStatus) {
        this.memoPostStatus = memoPostStatus;
    }

    public AccountingStatus getCustomerAccountingStatus() {
        return customerAccountingStatus;
    }

    public void setCustomerAccountingStatus(AccountingStatus customerAccountingStatus) {
        this.customerAccountingStatus = customerAccountingStatus;
    }

    public LocalDate getCustomerAccountingDate() {
        return customerAccountingDate;
    }

    public void setCustomerAccountingDate(LocalDate customerAccountingDate) {
        this.customerAccountingDate = customerAccountingDate;
    }

    public AccountingStatus getGlAccountingStatus() {
        return glAccountingStatus;
    }

    public void setGlAccountingStatus(AccountingStatus glAccountingStatus) {
        this.glAccountingStatus = glAccountingStatus;
    }

    public LocalDate getGlAccountingDate() {
        return glAccountingDate;
    }

    public void setGlAccountingDate(LocalDate glAccountingDate) {
        this.glAccountingDate = glAccountingDate;
    }

    public String getFedSettlementId() {
        return fedSettlementId;
    }

    public void setFedSettlementId(String fedSettlementId) {
        this.fedSettlementId = fedSettlementId;
    }

    public String getTransmissionStatus() {
        return transmissionStatus;
    }

    public void setTransmissionStatus(String transmissionStatus) {
        this.transmissionStatus = transmissionStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LocalDateTime getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    public void setLastUpdatedDateTime(LocalDateTime lastUpdatedDateTime) {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    @Override
    public String toString() {
        return "PaymentReversalRecord [id=" + id + ", achReversalId=" + achReversalId + ", returnId=" + returnId
                + ", payment=" + payment + ", reversalDate=" + reversalDate + ", reversalExpiryDate="
                + reversalExpiryDate + ", status=" + status + ", memoPostDate=" + memoPostDate + ", memoPostStatus="
                + memoPostStatus + ", customerAccountingStatus=" + customerAccountingStatus
                + ", customerAccountingDate=" + customerAccountingDate + ", glAccountingStatus=" + glAccountingStatus
                + ", glAccountingDate=" + glAccountingDate + ", fedSettlementId=" + fedSettlementId
                + ", transmissionStatus=" + transmissionStatus + ", createdBy=" + createdBy + ", initiatedBy="
                + initiatedBy + ", createdDateTime=" + createdDateTime + ", lastUpdatedBy=" + lastUpdatedBy
                + ", lastUpdatedDateTime=" + lastUpdatedDateTime + "]";
    }
}
