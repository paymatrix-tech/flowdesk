package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class SwiftDetails implements Serializable{

    private UUID id;

    private UUID messageId;

    private String subType;

    private Payment payment;

    private String status;

    private String error;

    private String cancellationDetails;

    public SwiftDetails() {}

    public SwiftDetails(SwiftDetails swiftDetails) {
        this.messageId = swiftDetails.getMessageId();
        this.subType = swiftDetails.getSubType();
        this.status = swiftDetails.getStatus();
        this.error = swiftDetails.getError();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCancellationDetails() {
        return cancellationDetails;
    }

    public void setCancellationDetails(String cancellationDetails) {
        this.cancellationDetails = cancellationDetails;
    }

}
