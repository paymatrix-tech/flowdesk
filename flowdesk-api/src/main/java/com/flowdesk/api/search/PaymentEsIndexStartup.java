package com.flowdesk.api.search;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/** Resolves ES field names before search/analytics/backfill run. */
@Component
@Order(0)
@RequiredArgsConstructor
public class PaymentEsIndexStartup implements ApplicationRunner {

    private final PaymentElasticsearchIndexSupport indexSupport;

    @Override
    public void run(ApplicationArguments args) {
        indexSupport.ensurePaymentsIndex();
    }
}
