package com.flowdesk.api.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowdesk.api.mapper.PaymentMapper;
import com.flowdesk.api.model.es.PaymentDocument;
import com.flowdesk.api.model.vo.Payment;
import com.flowdesk.api.repository.es.PaymentDocumentRepository;

import lombok.extern.slf4j.Slf4j;

import static com.flowdesk.api.common.CommonConstants.PAYMENT_INDEX;

@Slf4j
@Service
public class PaymentEsIndexer {

    private final PaymentDocumentRepository paymentDocumentRepository;
    private final PaymentElasticsearchIndexSupport indexSupport;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper cdcObjectMapper;
    private final int bulkBatchSize;

    public PaymentEsIndexer(
            PaymentDocumentRepository paymentDocumentRepository,
            PaymentElasticsearchIndexSupport indexSupport,
            ElasticsearchOperations elasticsearchOperations,
            @Qualifier("cdcObjectMapper") ObjectMapper cdcObjectMapper,
            @Value("${flowdesk.elasticsearch.bulk-batch-size:200}") int bulkBatchSize) {
        this.paymentDocumentRepository = paymentDocumentRepository;
        this.indexSupport = indexSupport;
        this.elasticsearchOperations = elasticsearchOperations;
        this.cdcObjectMapper = cdcObjectMapper;
        this.bulkBatchSize = Math.max(50, bulkBatchSize);
    }

    public void indexFromJson(JsonNode after) {
        if (after == null || after.isNull()) {
            return;
        }
        try {
            Payment payment = cdcObjectMapper.treeToValue(after, Payment.class);
            index(payment);
        } catch (Exception e) {
            log.warn("Failed to index payment from CDC/demo payload: {}", e.getMessage());
        }
    }

    public void index(Payment payment) {
        if (payment == null) {
            return;
        }
        PaymentDocument document = toDocument(payment);
        if (document == null) {
            return;
        }
        indexSupport.ensurePaymentsIndex();
        paymentDocumentRepository.save(document);
    }

    public void indexBatch(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return;
        }
        indexSupport.ensurePaymentsIndex();
        List<IndexQuery> queries = new ArrayList<>(payments.size());
        for (Payment payment : payments) {
            PaymentDocument document = toDocument(payment);
            if (document == null || document.getPaymentId() == null) {
                continue;
            }
            queries.add(IndexQuery.builder()
                    .withId(document.getPaymentId())
                    .withObject(document)
                    .build());
        }
        if (queries.isEmpty()) {
            return;
        }
        for (int i = 0; i < queries.size(); i += bulkBatchSize) {
            int end = Math.min(i + bulkBatchSize, queries.size());
            elasticsearchOperations.bulkIndex(
                    queries.subList(i, end),
                    IndexCoordinates.of(PAYMENT_INDEX));
        }
    }

    public int bulkBatchSize() {
        return bulkBatchSize;
    }

    private PaymentDocument toDocument(Payment payment) {
        PaymentDocument document = PaymentMapper.INSTANCE.toDocument(payment);
        if (document == null) {
            return null;
        }
        if (document.getPaymentId() == null && payment.getPaymentId() != null) {
            document.setPaymentId(String.valueOf(payment.getPaymentId()));
        }
        return document;
    }
}
