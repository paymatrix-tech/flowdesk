package com.flowdesk.api.search;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import com.flowdesk.api.model.es.PaymentDocument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentElasticsearchIndexSupport {

    private final ElasticsearchOperations elasticsearchOperations;
    private final PaymentEsFieldResolver fieldResolver;

    private volatile boolean initialized;

    public void ensurePaymentsIndex() {
        if (initialized) {
            return;
        }
        synchronized (this) {
            if (initialized) {
                return;
            }
            var indexOps = elasticsearchOperations.indexOps(PaymentDocument.class);
            if (!indexOps.exists()) {
                indexOps.createWithMapping();
                log.info("Created Elasticsearch index for payments with Flowdesk mappings");
            }
            fieldResolver.refreshFromIndex(indexOps);
            initialized = true;
        }
    }

    public void reset() {
        synchronized (this) {
            initialized = false;
        }
    }
}
