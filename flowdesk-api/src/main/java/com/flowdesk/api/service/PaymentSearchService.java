package com.flowdesk.api.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.flowdesk.api.dto.ESApiResponse;
import com.flowdesk.api.model.es.PaymentDocument;
import com.flowdesk.api.search.PaymentEsFieldResolver;
import com.flowdesk.api.search.PaymentEsQueryFields;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.flowdesk.api.common.CommonConstants.PAYMENT_INDEX;

@Service
@RequiredArgsConstructor
public class PaymentSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final PaymentEsFieldResolver fieldResolver;

    public ESApiResponse<PaymentDocument> searchByKeyword(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return new ESApiResponse<>(List.of(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, null);
        }

        String trimmed = keyword.trim();
        String statusField = fieldResolver.getPaymentStatusField();
        String paymentIdField = fieldResolver.getPaymentIdField();

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    b.should(s -> s.match(m -> m
                            .field(PaymentEsQueryFields.SEARCHABLE_TEXT)
                            .query(trimmed)
                            .operator(Operator.Or)));
                    b.should(s -> s.match(m -> m
                            .field(PaymentEsQueryFields.SENDER_NAME)
                            .query(trimmed)
                            .operator(Operator.Or)));
                    b.should(s -> s.match(m -> m
                            .field(PaymentEsQueryFields.RECEIVER_NAME)
                            .query(trimmed)
                            .operator(Operator.Or)));
                    b.should(s -> s.term(t -> t
                            .field(statusField)
                            .value(trimmed.toUpperCase())));
                    if (trimmed.chars().allMatch(Character::isDigit)) {
                        b.should(s -> s.term(t -> t
                                .field(paymentIdField)
                                .value(trimmed)));
                    }
                    b.minimumShouldMatch("1");
                    return b;
                }))
                .withPageable(pageable)
                .build();

        SearchHits<PaymentDocument> searchHits =
                elasticsearchOperations.search(query, PaymentDocument.class, IndexCoordinates.of(PAYMENT_INDEX));

        List<PaymentDocument> results = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        long totalHits = searchHits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / pageable.getPageSize());

        return new ESApiResponse<>(results,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                totalHits,
                totalPages,
                null
        );
    }
}
