package com.flowdesk.api.controller;

import com.flowdesk.api.dto.ESApiResponse;
import com.flowdesk.api.model.es.PaymentDocument;
import com.flowdesk.api.service.PaymentSearchService;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search/payments")
public class PaymentSearchController {

    private final PaymentSearchService paymentSearchService;

    @GetMapping("/by-keyword")
    public ResponseEntity<ESApiResponse<PaymentDocument>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Payment search request received, keyword-[{}], page-[{}], size-[{}]", keyword, page, size);
        Pageable pageable = PageRequest.of(page, size);
        ESApiResponse<PaymentDocument> response = paymentSearchService.searchByKeyword(keyword, pageable);
        log.debug("Returning search result - {}", response);
        return ResponseEntity.ok(response);
    }
}
