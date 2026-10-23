package com.flowdesk.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import com.flowdesk.api.dto.ESApiResponse;
import com.flowdesk.api.dto.PaymentAnalyticsSummary;
import com.flowdesk.api.model.es.PaymentDocument;
import com.flowdesk.api.search.PaymentEsFieldResolver;
import com.flowdesk.api.search.PaymentEsQueryFields;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.flowdesk.api.common.CommonConstants.ONE_MILLION;
import static com.flowdesk.api.common.CommonConstants.PAYMENT_INDEX;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAnalyticsService {

    private static final List<String> FAILED_STATUSES = List.of(
            "ACH_PROCESSING_FAILED",
            "CANCELLATION_FAILED",
            "CANCELLATION_REQUEST_FAILED",
            "CREATE_PAYMENT_FAILED",
            "COMPLIANCE_REQUEST_FAILURE",
            "FRAUD_REQUEST_FAILED",
            "INITIATE_FAILED",
            "TRANSMISSION_FAILED",
            "VALIDATION_FAILED"
    );

    private static final List<String> REVIEW_STATUSES = List.of(
            "APPROVE",
            "BLOCKED",
            "COMPLIANCE_REVIEW",
            "COMPLIANCE_SEIZED",
            "FRAUD_REJECTED",
            "FRAUD_SEIZED",
            "HOLD",
            "OVERRIDE",
            "PARTIALLY_APPROVED",
            "PENDING_APPROVAL",
            "PENDING_CALLBACK",
            "VALIDATION_REVIEW"
    );

    private static final List<String> FLAGGED_STATUSES = List.of(
            "COMPLIANCE_REJECTED",
            "COMPLIANCE_SEIZED",
            "FRAUD_REJECTED",
            "FRAUD_SEIZED"
    );

    private final ElasticsearchOperations elasticsearchOperations;
    private final PaymentEsFieldResolver fieldResolver;

    public PaymentAnalyticsSummary getDashboardSummary() {
        CompletableFuture<Long> highValue = CompletableFuture.supplyAsync(this::countHighValue);
        CompletableFuture<Long> failed = CompletableFuture.supplyAsync(this::countFailed);
        CompletableFuture<Long> pending = CompletableFuture.supplyAsync(this::countPendingReview);
        CompletableFuture<Long> flagged = CompletableFuture.supplyAsync(this::countFlagged);
        return CompletableFuture.allOf(highValue, failed, pending, flagged)
                .thenApply(v -> new PaymentAnalyticsSummary(
                        highValue.join(),
                        failed.join(),
                        pending.join(),
                        flagged.join()))
                .join();
    }

    public ESApiResponse<PaymentDocument> findHighValueTransactions(Double minAmount, Pageable pageable) {
        if (minAmount == null) {
            minAmount = ONE_MILLION;
        }
        Double finalMinAmount = minAmount;
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .should(s -> s.range(RangeQuery.of(r -> r.number(n -> n
                                .field(PaymentEsQueryFields.SENDER_AMOUNT)
                                .gte(finalMinAmount)))))
                        .should(s -> s.range(RangeQuery.of(r -> r.number(n -> n
                                .field(PaymentEsQueryFields.RECEIVER_AMOUNT)
                                .gte(finalMinAmount)))))
                        .minimumShouldMatch("1")))
                .withPageable(pageable)
                .build();
        return executeSearch(query, pageable, null);
    }

    public ESApiResponse<PaymentDocument> findFailedPayments(Pageable pageable) {
        return searchByStatuses(pageable, FAILED_STATUSES, true);
    }

    public ESApiResponse<PaymentDocument> findPendingReviewPayments(Pageable pageable) {
        return searchByStatuses(pageable, REVIEW_STATUSES, false);
    }

    public ESApiResponse<PaymentDocument> findComplianceFraudPayments(Pageable pageable) {
        return searchByStatuses(pageable, FLAGGED_STATUSES, false);
    }

    private long countHighValue() {
        return countQuery(highValueCountQuery(ONE_MILLION));
    }

    private long countFailed() {
        return countQuery(statusCountQuery(FAILED_STATUSES, true));
    }

    private long countPendingReview() {
        return countQuery(statusCountQuery(REVIEW_STATUSES, false));
    }

    private long countFlagged() {
        return countQuery(statusCountQuery(FLAGGED_STATUSES, false));
    }

    private NativeQuery highValueCountQuery(double minAmount) {
        return NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .should(s -> s.range(RangeQuery.of(r -> r.number(n -> n
                                .field(PaymentEsQueryFields.SENDER_AMOUNT)
                                .gte(minAmount)))))
                        .should(s -> s.range(RangeQuery.of(r -> r.number(n -> n
                                .field(PaymentEsQueryFields.RECEIVER_AMOUNT)
                                .gte(minAmount)))))
                        .minimumShouldMatch("1")))
                .build();
    }

    private NativeQuery statusCountQuery(List<String> statuses, boolean includeMemoFailure) {
        String statusField = fieldResolver.getPaymentStatusField();
        String memoField = fieldResolver.getMemopostStatusField();
        return NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    b.should(s -> s.terms(t -> t
                            .field(statusField)
                            .terms(v -> v.value(statuses.stream().map(FieldValue::of).toList()))));
                    if (includeMemoFailure) {
                        b.should(s -> s.term(t -> t.field(memoField).value("FAILURE")));
                    }
                    b.minimumShouldMatch("1");
                    return b;
                }))
                .build();
    }

    private long countQuery(NativeQuery query) {
        return elasticsearchOperations.count(query, IndexCoordinates.of(PAYMENT_INDEX));
    }

    private ESApiResponse<PaymentDocument> searchByStatuses(
            Pageable pageable,
            List<String> statuses,
            boolean includeMemoFailure) {
        String statusField = fieldResolver.getPaymentStatusField();
        String memoField = fieldResolver.getMemopostStatusField();
        boolean withAggs = pageable.getPageSize() > 0;
        NativeQueryBuilder builder = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    b.should(s -> s.terms(t -> t
                            .field(statusField)
                            .terms(v -> v.value(statuses.stream().map(FieldValue::of).toList()))));
                    if (includeMemoFailure && memoField != null) {
                        b.should(s -> s.term(t -> t.field(memoField).value("FAILURE")));
                    }
                    b.minimumShouldMatch("1");
                    return b;
                }))
                .withPageable(pageable);
        if (withAggs) {
            builder.withAggregation("by_paymentStatus",
                    Aggregation.of(a -> a.terms(t -> t.field(statusField))
                            .aggregations("total_value",
                                    a2 -> a2.sum(s -> s.field(PaymentEsQueryFields.SENDER_AMOUNT)))));
        }
        return executeSearch(builder.build(), pageable, withAggs);
    }

    private ESApiResponse<PaymentDocument> executeSearch(
            Query query,
            Pageable pageable,
            Boolean extractAggs) {
        SearchHits<PaymentDocument> searchHits =
                elasticsearchOperations.search(query, PaymentDocument.class, IndexCoordinates.of(PAYMENT_INDEX));
        List<PaymentDocument> results = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        long totalHits = searchHits.getTotalHits();
        int pageSize = Math.max(pageable.getPageSize(), 1);
        int totalPages = (int) Math.ceil((double) totalHits / pageSize);
        Map<String, Map<String, Object>> aggregations = Boolean.TRUE.equals(extractAggs)
                ? extractStatusAggregations(searchHits.getAggregations())
                : null;
        return new ESApiResponse<>(
                results,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                totalHits,
                totalPages,
                aggregations
        );
    }

    private Map<String, Map<String, Object>> extractStatusAggregations(AggregationsContainer<?> aggsContainer) {
        Map<String, Map<String, Object>> aggregations = new HashMap<>();
        if (!(aggsContainer instanceof ElasticsearchAggregations elasticsearchAggs)) {
            return aggregations;
        }
        ElasticsearchAggregation paymentStatusAgg = elasticsearchAggs.get("by_paymentStatus");
        if (paymentStatusAgg == null) {
            return aggregations;
        }
        Aggregate aggregate = paymentStatusAgg.aggregation().getAggregate();
        if (!aggregate.isSterms()) {
            return aggregations;
        }
        StringTermsAggregate terms = aggregate.sterms();
        for (StringTermsBucket bucket : terms.buckets().array()) {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("count", bucket.docCount());
            Aggregate totalValueAgg = bucket.aggregations().get("total_value");
            if (totalValueAgg != null && totalValueAgg.isSum()) {
                BigDecimal totalValue = BigDecimal.valueOf(totalValueAgg.sum().value())
                        .setScale(2, RoundingMode.HALF_UP);
                metrics.put("totalValue", totalValue);
            }
            aggregations.put(bucket.key().stringValue(), metrics);
        }
        return aggregations;
    }
}
