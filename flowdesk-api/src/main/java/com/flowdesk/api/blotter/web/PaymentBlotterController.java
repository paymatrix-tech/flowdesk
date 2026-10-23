package com.flowdesk.api.blotter.web;

import com.flowdesk.api.blotter.dto.ApiResponse;
import com.flowdesk.api.blotter.dto.PaginatedApiResponse;
import com.flowdesk.api.blotter.dto.PaymentCsvRequest;
import com.flowdesk.api.blotter.dto.PaymentSearchFiltersDto;
import com.flowdesk.api.blotter.dto.PaymentSearchRequest;
import com.flowdesk.api.blotter.service.PaymentBlotterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentBlotterController {

    private final PaymentBlotterService paymentBlotterService;

    @PostMapping("/search")
    public PaginatedApiResponse search(@Valid @RequestBody PaymentSearchRequest request) {
        return paymentBlotterService.search(request);
    }

    @PostMapping("/export/csv")
    public ResponseEntity<String> exportCsv(@Valid @RequestBody PaymentCsvRequest request) {
        String csv = paymentBlotterService.exportCsv(request);
        String filename = "payments_export_" + LocalDate.now() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/stats/summary")
    public ApiResponse<Map<String, Object>> stats(@RequestParam Map<String, String> query) {
        PaymentSearchFiltersDto filters = mapQueryFilters(query);
        return ApiResponse.ok(paymentBlotterService.stats(filters), "Payment statistics retrieved successfully");
    }

    @GetMapping("/columns/available")
    public ApiResponse<Map<String, Object>> columns() {
        return ApiResponse.ok(Map.of("columns", paymentBlotterService.availableColumns()),
                "Available columns retrieved successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> byId(@PathVariable long id) {
        return ApiResponse.ok(paymentBlotterService.getById(id), "Payment retrieved successfully");
    }

    private PaymentSearchFiltersDto mapQueryFilters(Map<String, String> query) {
        PaymentSearchFiltersDto filters = new PaymentSearchFiltersDto();
        query.forEach((key, value) -> {
            if (key.startsWith("filters[") && key.endsWith("]")) {
                String field = key.substring(8, key.length() - 1);
                switch (field) {
                    case "payment_status" -> filters.setPayment_status(value);
                    case "delivery_method" -> filters.setDelivery_method(value);
                    case "payment_type" -> filters.setPayment_type(value);
                    case "business_unit" -> filters.setBusiness_unit(value);
                    default -> { }
                }
            }
        });
        return filters;
    }
}
