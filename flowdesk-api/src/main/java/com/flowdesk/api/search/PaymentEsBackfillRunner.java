package com.flowdesk.api.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowdesk.api.model.vo.Payment;

import lombok.extern.slf4j.Slf4j;

import static com.flowdesk.api.common.CommonConstants.PAYMENT_INDEX;
import static com.flowdesk.api.common.CommonConstants.PAYMENT_TABLE;

@Slf4j
@Component
@Order(1)
public class PaymentEsBackfillRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PaymentEsIndexer paymentEsIndexer;
    private final PaymentElasticsearchIndexSupport indexSupport;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper cdcObjectMapper;

    @Value("${flowdesk.elasticsearch.backfill-when-empty:true}")
    private boolean backfillWhenEmpty;

    @Value("${flowdesk.elasticsearch.backfill-limit:500}")
    private int backfillLimit;

    public PaymentEsBackfillRunner(
            JdbcTemplate jdbcTemplate,
            PaymentEsIndexer paymentEsIndexer,
            PaymentElasticsearchIndexSupport indexSupport,
            ElasticsearchOperations elasticsearchOperations,
            @Qualifier("cdcObjectMapper") ObjectMapper cdcObjectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.paymentEsIndexer = paymentEsIndexer;
        this.indexSupport = indexSupport;
        this.elasticsearchOperations = elasticsearchOperations;
        this.cdcObjectMapper = cdcObjectMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!backfillWhenEmpty) {
            return;
        }
        try {
            indexSupport.ensurePaymentsIndex();
            long count = elasticsearchOperations.count(Query.findAll(), IndexCoordinates.of(PAYMENT_INDEX));
            if (count > 0) {
                log.debug("Elasticsearch payments index has {} documents; skipping backfill", count);
                return;
            }
            int limit = Math.max(1, Math.min(backfillLimit, 5000));
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM " + PAYMENT_TABLE + " ORDER BY payment_id DESC LIMIT ?", limit);
            if (rows.isEmpty()) {
                log.info("No payments in database to backfill into Elasticsearch");
                return;
            }
            log.info("Backfilling {} payments into Elasticsearch index '{}'", rows.size(), PAYMENT_INDEX);
            List<Payment> batch = new ArrayList<>(paymentEsIndexer.bulkBatchSize());
            for (Map<String, Object> row : rows) {
                Payment payment = cdcObjectMapper.convertValue(row, Payment.class);
                if (payment == null) {
                    continue;
                }
                batch.add(payment);
                if (batch.size() >= paymentEsIndexer.bulkBatchSize()) {
                    paymentEsIndexer.indexBatch(batch);
                    batch.clear();
                }
            }
            paymentEsIndexer.indexBatch(batch);
            log.info("Elasticsearch backfill complete");
        } catch (Exception e) {
            log.warn("Elasticsearch backfill skipped or failed: {}", e.getMessage());
        }
    }
}
