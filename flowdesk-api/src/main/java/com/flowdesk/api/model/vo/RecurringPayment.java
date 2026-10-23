package com.flowdesk.api.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.flowdesk.api.util.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RecurringPayment {

    private long recurranceId;

    private String externalRecurrenceId;

    private Boolean active;

    private RecurringFrequency frequency;

    private LocalDate startDate;

    private RecurranceType type;

    private Long noOfPayments;

    private LocalDate endDate;

    private LocalDate originalNextPaymentDate;

    private LocalDate nextPaymentDate;

    private Long lastPaymentReference;

    private String createdBy;

    private String lastUpdatedBy;

    private LocalDateTime creationDateTime;

    private LocalDateTime lastUpdatedTime;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public RecurringFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurringFrequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public RecurranceType getType() {
        return type;
    }

    public void setType(RecurranceType type) {
        this.type = type;
    }

    public long getNoOfPayments() {
        return noOfPayments;
    }

    public void setNoOfPayments(Long noOfPayments) {
        this.noOfPayments = noOfPayments;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public LocalDate getOriginalNextPaymentDate() {
        return originalNextPaymentDate;
    }

    public void setOriginalNextPaymentDate(LocalDate originalNextPaymentDate) {
        this.originalNextPaymentDate = originalNextPaymentDate;
    }

    public long getRecurranceId() {
        return recurranceId;
    }

    public void setRecurranceId(long recurranceId) {
        this.recurranceId = recurranceId;
    }

    public String getExternalRecurrenceId() {
        return externalRecurrenceId;
    }

    public void setExternalRecurrenceId(String externalRecurrenceId) {
        this.externalRecurrenceId = externalRecurrenceId;
    }

    public Long getLastPaymentReference() {
        return lastPaymentReference;
    }

    public void setLastPaymentReference(Long lastPaymentReference) {
        this.lastPaymentReference = lastPaymentReference;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    @Override
    public String toString() {
        return "RecurringPayment{" +
                "recurranceId=" + recurranceId +
                ", externalRecurrenceId=" + externalRecurrenceId +
                ", active=" + active +
                ", frequency=" + frequency +
                ", startDate=" + startDate +
                ", type=" + type +
                ", noOfPayments=" + noOfPayments +
                ", endDate=" + endDate +
                ", originalNextPaymentDate=" + originalNextPaymentDate +
                ", nextPaymentDate=" + nextPaymentDate +
                ", lastPaymentReference=" + lastPaymentReference +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }
}
