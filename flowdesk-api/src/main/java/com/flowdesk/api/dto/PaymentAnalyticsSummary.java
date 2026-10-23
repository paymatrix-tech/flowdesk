package com.flowdesk.api.dto;

public record PaymentAnalyticsSummary(
        long highValue,
        long failed,
        long pendingReview,
        long flagged
) {}
