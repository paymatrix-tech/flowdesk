package com.flowdesk.api.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.UUID;

public class FedNowDetails implements Serializable {

    private UUID id;

    private Payment payment;

    private String fedNowMessage;

    private String returnStatus;

    private String comments;

    private String msgId;

    public FedNowDetails() {
    }

    public FedNowDetails(FedNowDetails fedNowDetails) {
        ObjectMapper objectMapper=new ObjectMapper();

        try {
            fedNowMessage = objectMapper.readValue(objectMapper.writeValueAsString(fedNowDetails.getFedNowMessage()),String.class);
        } catch (Exception e) {
            fedNowMessage=fedNowDetails.getFedNowMessage();
        }

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getFedNowMessage() {
        return fedNowMessage;
    }

    public void setFedNowMessage(String fedNowMessage) {
        this.fedNowMessage = fedNowMessage;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "FedNowDetails [id=" + id + ", payment=" + payment + ", fedNowMessage=" + fedNowMessage
                + ", returnStatus=" + returnStatus + ", comments=" + comments + ", msgId=" + msgId + "]";
    }

}
