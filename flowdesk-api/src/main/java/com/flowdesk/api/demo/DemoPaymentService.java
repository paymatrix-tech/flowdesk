package com.flowdesk.api.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static com.flowdesk.api.common.CommonConstants.PAYMENT_INDEX;
import static com.flowdesk.api.common.CommonConstants.PAYMENT_TABLE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowdesk.api.live.PaymentLiveEventPublisher;
import com.flowdesk.api.search.PaymentElasticsearchIndexSupport;
import com.flowdesk.api.search.PaymentEsIndexer;

@Service
@ConditionalOnProperty(name = "flowdesk.demo.payments.enabled", havingValue = "true")
public class DemoPaymentService {

    private static final List<String> STATUSES = List.of(
            "PROCESSED", "PENDING_APPROVAL", "VALIDATION_FAILED", "COMPLIANCE_REJECTED",
            "TRANSMISSION_FAILED", "HOLD", "APPROVE", "FRAUD_REJECTED"
    );
    private static final List<String> TYPES = List.of("WIRE", "ACH", "RTP", "BOOK");
    private static final List<String> METHODS = List.of("FEDWIRE", "ACH", "FEDNOW", "SWIFT");
    private static final List<String> CHANNELS = List.of("ONLINE", "BRANCH", "API", "FILE");

    private final JdbcTemplate jdbcTemplate;
    private final DemoPaymentProperties properties;
    private final PaymentLiveEventPublisher livePublisher;
    private final PaymentEsIndexer paymentEsIndexer;
    private final PaymentElasticsearchIndexSupport paymentElasticsearchIndexSupport;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;
    private final TaskExecutor liveNotifyExecutor = new SimpleAsyncTaskExecutor("demo-live-");

    public DemoPaymentService(
            JdbcTemplate jdbcTemplate,
            DemoPaymentProperties properties,
            PaymentLiveEventPublisher livePublisher,
            PaymentEsIndexer paymentEsIndexer,
            PaymentElasticsearchIndexSupport paymentElasticsearchIndexSupport,
            ElasticsearchOperations elasticsearchOperations,
            @Qualifier("cdcObjectMapper") ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.livePublisher = livePublisher;
        this.paymentEsIndexer = paymentEsIndexer;
        this.paymentElasticsearchIndexSupport = paymentElasticsearchIndexSupport;
        this.elasticsearchOperations = elasticsearchOperations;
        this.objectMapper = objectMapper;
    }

    public DemoFlushResult flushAll() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + PAYMENT_TABLE, Long.class);
        // DELETE (not TRUNCATE) so debezium_user can flush without owning the sequence
        jdbcTemplate.update("DELETE FROM " + PAYMENT_TABLE);
        boolean esCleared = false;
        var indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(PAYMENT_INDEX));
        if (indexOps.exists()) {
            indexOps.delete();
            esCleared = true;
        }
        paymentElasticsearchIndexSupport.reset();
        return new DemoFlushResult(count != null ? count : 0, esCleared);
    }

    public DemoFlushResult flushAndSeed(int seedCount) {
        DemoFlushResult flushed = flushAll();
        int n = Math.min(Math.max(seedCount, 0), 500);
        for (int i = 0; i < n; i++) {
            insertOne(true);
        }
        return flushed;
    }

    public DemoBurstResult runBurst(int durationSeconds, int intervalMs, boolean notifyLive) {
        int seconds = Math.min(Math.max(durationSeconds, 1), properties.maxBurstSeconds());
        boolean maxThroughput = intervalMs <= 0;
        int throttleMs = maxThroughput ? 0 : Math.max(intervalMs, properties.minIntervalMs());
        int inserted = 0;
        long start = System.currentTimeMillis();
        long deadline = start + seconds * 1000L;
        long nextAt = start;
        while (System.currentTimeMillis() < deadline) {
            insertOne(notifyLive);
            inserted++;
            if (maxThroughput || throttleMs <= 0) {
                continue;
            }
            nextAt += throttleMs;
            long sleepMs = nextAt - System.currentTimeMillis();
            if (sleepMs <= 0) {
                continue;
            }
            if (nextAt > deadline) {
                break;
            }
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        long elapsed = Math.max(1, System.currentTimeMillis() - start);
        double rate = inserted * 1000.0 / elapsed;
        return new DemoBurstResult(inserted, elapsed, rate, maxThroughput);
    }

    void insertOne(boolean notifyLive) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        String txnId = "TXN-DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String status = STATUSES.get(r.nextInt(STATUSES.size()));
        String type = TYPES.get(r.nextInt(TYPES.size()));
        String method = METHODS.get(r.nextInt(METHODS.size()));
        String channel = CHANNELS.get(r.nextInt(CHANNELS.size()));
        BigDecimal amount = BigDecimal.valueOf(r.nextInt(500, 500_000), 2);
        String sender = "Sender " + r.nextInt(1000, 9999);
        String receiver = "Receiver " + r.nextInt(1000, 9999);
        String unit = r.nextBoolean() ? "Retail" : "Commercial";
        String memo = status.contains("FAIL") ? "FAILURE" : "SUCCESS";
        LocalDate paymentDate = LocalDate.now();
        LocalDate deliveryDate = LocalDate.now().plusDays(r.nextInt(0, 5));
        LocalDateTime created = LocalDateTime.now();

        String insertSql = """
                INSERT INTO %s (
                    transaction_id, payment_status, payment_type, payment_io_type, delivery_method,
                    entry_type, channel, business_unit, department, counter_party,
                    sender_name, sender_amount, sender_currency, receiver_name, receiver_amount, receiver_currency,
                    payment_date, delivery_date, creation_date_time, memo_post_status,
                    payment_note, book, cost_center, customer_type
                ) VALUES (?, ?, ?, 'OUT', ?, 'DEBIT', ?, ?, 'Payments', ?, ?, ?, 'USD', ?, ?, 'USD',
                    ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING payment_id
                """.formatted(PAYMENT_TABLE);
        Long paymentId = jdbcTemplate.queryForObject(
                insertSql,
                Long.class,
                txnId,
                status,
                type,
                method,
                channel,
                unit,
                "Counterparty " + r.nextInt(100, 999),
                sender,
                amount,
                receiver,
                amount,
                paymentDate,
                deliveryDate,
                created,
                memo,
                "UI-triggered demo payment",
                "BOOK-" + r.nextInt(1, 20),
                "CC-" + r.nextInt(100, 999),
                r.nextBoolean() ? "RETAIL" : "CORPORATE"
        );

        ObjectNode after = objectMapper.createObjectNode();
        after.put("payment_id", paymentId);
        after.put("transaction_id", txnId);
        after.put("payment_status", status);
        after.put("payment_type", type);
        after.put("payment_io_type", "OUT");
        after.put("delivery_method", method);
        after.put("channel", channel);
        after.put("business_unit", unit);
        after.put("sender_name", sender);
        after.put("sender_amount", amount);
        after.put("sender_currency", "USD");
        after.put("receiver_name", receiver);
        after.put("receiver_amount", amount);
        after.put("receiver_currency", "USD");
        after.put("payment_date", paymentDate.toString());
        after.put("delivery_date", deliveryDate.toString());
        after.put("memo_post_status", memo);
        paymentEsIndexer.indexFromJson(after);
        if (!notifyLive) {
            return;
        }
        liveNotifyExecutor.execute(() -> livePublisher.publishCreate(after));
    }
}
