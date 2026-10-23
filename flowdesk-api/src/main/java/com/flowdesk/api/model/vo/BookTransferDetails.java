package com.flowdesk.api.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class BookTransferDetails {

    private UUID id;

    private Payment payment;

    private boolean isReversal;

    private boolean isOffset;

    public BookTransferDetails() {
    }
    public BookTransferDetails(BookTransferDetails bookTransferDetails){
        this.id=bookTransferDetails.getId();
        this.payment=bookTransferDetails.getPayment();
        this.isReversal=bookTransferDetails.getIsReversal();
        this.isOffset=bookTransferDetails.getIsOffset();
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

    public boolean getIsReversal() {
        return isReversal;
    }

    public void setReversal(boolean reversal) {
        isReversal = reversal;
    }

    public boolean getIsOffset() {
        return isOffset;
    }

    public void setOffset(boolean offset) {
        isOffset = offset;
    }

    @Override
    public String toString() {
        return "BookTransferDetails{" +
                "id=" + id +
                ", payment=" + payment +
                ", isReversal=" + isReversal +
                ", isOffset=" + isOffset +
                '}';
    }
}
