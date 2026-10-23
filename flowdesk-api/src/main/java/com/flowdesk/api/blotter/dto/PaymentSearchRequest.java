package com.flowdesk.api.blotter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class PaymentSearchRequest {
    @Min(1)
    private int page = 1;
    @Min(1)
    @Max(100)
    private int limit = 20;
    private List<String> columns;
    private PaymentSearchFiltersDto filters;
    private String sort_by = "payment_id";
    private String sort_order = "desc";
    private String search;
}
