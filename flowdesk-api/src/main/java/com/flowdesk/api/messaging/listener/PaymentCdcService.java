package com.flowdesk.api.messaging.listener;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import static com.flowdesk.api.common.CommonConstants.PAYMENT_CDC_GROUP;
import static com.flowdesk.api.common.CommonConstants.PAYMENT_CDC_TOPIC;
import com.flowdesk.api.live.PaymentLiveEventPublisher;
import com.flowdesk.api.search.PaymentEsIndexer;
import com.flowdesk.api.util.CdcOperationType;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

@Slf4j
@Service
public class PaymentCdcService {
    private final PaymentEsIndexer paymentEsIndexer;
    private final ObjectMapper objectMapper;
    private final PaymentLiveEventPublisher livePublisher;
    private final TaskExecutor cdcTaskExecutor;

    public PaymentCdcService(
            PaymentEsIndexer paymentEsIndexer,
            @Qualifier("cdcObjectMapper") ObjectMapper objectMapper,
            PaymentLiveEventPublisher livePublisher,
            @Qualifier("cdcTaskExecutor") TaskExecutor cdcTaskExecutor) {
        this.paymentEsIndexer = paymentEsIndexer;
        this.objectMapper = objectMapper;
        this.livePublisher = livePublisher;
        this.cdcTaskExecutor = cdcTaskExecutor;
    }

    @KafkaListener(topics = PAYMENT_CDC_TOPIC, groupId = PAYMENT_CDC_GROUP)
    public void processPaymentEvent(String event) {
        try {
            JsonNode root = objectMapper.readTree(event);
            CdcOperationType operation = CdcOperationType.fromOp(root.get("op").asText());
            JsonNode after = root.get("after");
            if (after == null || after.isNull()) {
                return;
            }
            if (operation == CdcOperationType.C || operation == CdcOperationType.U) {
                JsonNode afterCopy = after.deepCopy();
                cdcTaskExecutor.execute(() -> {
                    try {
                        paymentEsIndexer.indexFromJson(afterCopy);
                        livePublisher.publishRaw(event);
                    } catch (Exception e) {
                        log.error("Async CDC processing failed", e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Failed to parse Kafka CDC event", e);
        }
    }
}
