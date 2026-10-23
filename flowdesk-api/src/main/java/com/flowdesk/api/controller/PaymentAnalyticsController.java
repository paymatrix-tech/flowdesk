package com.flowdesk.api.controller;

import com.flowdesk.api.dto.ESApiResponse;
import com.flowdesk.api.dto.PaymentAnalyticsSummary;
import com.flowdesk.api.model.es.PaymentDocument;
import com.flowdesk.api.service.PaymentAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/analytics/payments")
@RequiredArgsConstructor
public class PaymentAnalyticsController {

    private final PaymentAnalyticsService paymentAnalyticsService;

    @GetMapping("/summary")
    public ResponseEntity<PaymentAnalyticsSummary> getDashboardSummary() {
        return ResponseEntity.ok(paymentAnalyticsService.getDashboardSummary());
    }

    @GetMapping("/high-value")
    public ResponseEntity<ESApiResponse<PaymentDocument>> getHighValueTransactions(
            @RequestParam(required = false) Double minAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("High-value transaction request received, minAmount-[{}], page-[{}], size-[{}]", minAmount, page, size);
        Pageable pageable = PageRequest.of(page, size);
        ESApiResponse<PaymentDocument> response = paymentAnalyticsService.findHighValueTransactions(minAmount, pageable);
        log.debug("Returning high-value transactions - {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failed")
    public ResponseEntity<ESApiResponse<PaymentDocument>> getFailedPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Failed payment request received, page-[{}], size-[{}]", page, size);
        Pageable pageable = PageRequest.of(page, size);
        ESApiResponse<PaymentDocument> response = paymentAnalyticsService.findFailedPayments(pageable);
        log.debug("Returning response for failed payments - {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-review")
    public ResponseEntity<ESApiResponse<PaymentDocument>> getPendingReviewPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Pending review payment request received, page-[{}], size-[{}]", page, size);
        Pageable pageable = PageRequest.of(page, size);
        ESApiResponse<PaymentDocument> response = paymentAnalyticsService.findPendingReviewPayments(pageable);
        log.debug("Returning response for pending review payments - {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/flagged")
    public ResponseEntity<ESApiResponse<PaymentDocument>> getFlaggedPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Flagged payment request received, page-[{}], size-[{}]", page, size);
        Pageable pageable = PageRequest.of(page, size);
        ESApiResponse<PaymentDocument> response = paymentAnalyticsService.findComplianceFraudPayments(pageable);
        log.debug("Returning response for flagged payments - {}", response);
        return ResponseEntity.ok(response);
    }
}
