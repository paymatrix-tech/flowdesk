package com.flowdesk.api.blotter.dto;

import lombok.Data;

@Data
public class PaymentSearchFiltersDto {
    private String payment_status;
    private String delivery_method;
    private String payment_type;
    private String payment_io_type;
    private String channel;
    private String business_unit;
    private String department;
    private String counter_party;
    private String sender_name;
    private String receiver_name;
    private String transaction_id;
    private Double min_amount;
    private Double max_amount;
    private String sender_currency;
    private String receiver_currency;
    private String payment_date_from;
    private String payment_date_to;
    private String delivery_date_from;
    private String delivery_date_to;
    private String created_from;
    private String created_to;
}
