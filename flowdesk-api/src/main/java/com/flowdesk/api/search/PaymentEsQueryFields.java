package com.flowdesk.api.search;

/**
 * Field names for queries against {@code payments-mappings.json}.
 * Do not use bare analyzed text fields with {@code term}/{@code terms} aggregations.
 */
public final class PaymentEsQueryFields {

    private PaymentEsQueryFields() {}

    public static final String PAYMENT_STATUS = "paymentStatus";
    public static final String MEMOPOST_STATUS = "memopostStatus";
    public static final String PAYMENT_ID = "paymentId";
    public static final String SEARCHABLE_TEXT = "searchableText";
    public static final String SENDER_NAME = "senderName";
    public static final String RECEIVER_NAME = "receiverName";
    public static final String SENDER_AMOUNT = "senderAmount";
    public static final String RECEIVER_AMOUNT = "receiverAmount";
}
