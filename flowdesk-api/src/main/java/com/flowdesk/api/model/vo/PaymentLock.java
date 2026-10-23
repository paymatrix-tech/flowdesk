package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PaymentLock  implements Serializable  {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String lockedUserId;

    private boolean overrideRequestPending;

    private String rejectedUserIds;

    private String paymentLockTabToken;

    private Payment payment;

    /**
     * @return the lockedUserId
     */
    public String getLockedUserId() {
        return lockedUserId;
    }

    /**
     * @param lockedUserId the lockedUserId to set
     */
    public void setLockedUserId(String lockedUserId) {
        this.lockedUserId = lockedUserId;
    }

    /**
     * @return the overrideRequestPending
     */
    public boolean isOverrideRequestPending() {
        return overrideRequestPending;
    }

    /**
     * @param overrideRequestPending the overrideRequestPending to set
     */
    public void setOverrideRequestPending(boolean overrideRequestPending) {
        this.overrideRequestPending = overrideRequestPending;
    }

    /**
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the rejectedUserIds
     */
    public String getRejectedUserIds() {
        return rejectedUserIds;
    }

    /**
     * @param rejectedUserIds the rejectedUserIds to set
     */
    public void setRejectedUserIds(String rejectedUserIds) {
        this.rejectedUserIds = rejectedUserIds;
    }

    /**
     * @return the paymentLockTabToken
     */
    public String getPaymentLockTabToken() {
        return paymentLockTabToken;
    }

    /**
     * @param paymentLockTabToken the paymentLockTabToken to set
     */
    public void setPaymentLockTabToken(String paymentLockTabToken) {
        this.paymentLockTabToken = paymentLockTabToken;
    }

    public PaymentLock() {
        super();
    }

    public PaymentLock(UUID id, String lockedUserId, boolean overrideRequestPending,
                       String rejectedUserIds, String paymentLockTabToken) {
        super();
        this.id = id;
        this.lockedUserId = lockedUserId;
        this.overrideRequestPending = overrideRequestPending;
        this.rejectedUserIds = rejectedUserIds;
        this.paymentLockTabToken = paymentLockTabToken;
    }

    @Override
    public String toString() {
        return "PaymentLock [id=" + id + ", lockedUserId=" + lockedUserId
                + ", overrideRequestPending=" + overrideRequestPending + ", rejectedUserIds=" + rejectedUserIds
                + ", paymentLockTabToken=" + paymentLockTabToken + "]";
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

}
