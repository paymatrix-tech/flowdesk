package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RtpDetails implements Serializable {

    //@Type(type="uuid-char")
    private UUID id;

    private Payment payment;

    private String rtpMessage;

    private String returnStatus;

    private String comments;

    private String msgId;

    public RtpDetails() {
    }

    public RtpDetails(RtpDetails rtpDetails) {
        rtpMessage = rtpDetails.getRtpMessage();
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

    public String getRtpMessage() {
        return rtpMessage;
    }

    public void setRtpMessage(String rtpMessage) {
        this.rtpMessage = rtpMessage;
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

}
