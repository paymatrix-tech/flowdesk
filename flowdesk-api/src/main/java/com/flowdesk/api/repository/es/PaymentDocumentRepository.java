package com.flowdesk.api.repository.es;

import com.flowdesk.api.model.es.PaymentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDocumentRepository extends ElasticsearchRepository<PaymentDocument, String> {
}
