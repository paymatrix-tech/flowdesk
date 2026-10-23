package com.flowdesk.api.live;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.SneakyThrows;

@Component
public class PaymentLiveEventPublisher {

    private final PaymentLiveBroadcaster broadcaster;
    private final ObjectMapper objectMapper;

    public PaymentLiveEventPublisher(
            PaymentLiveBroadcaster broadcaster,
            @Qualifier("cdcObjectMapper") ObjectMapper objectMapper) {
        this.broadcaster = broadcaster;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public void publishCreate(ObjectNode after) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("op", "c");
        root.set("after", after);
        root.put("ts_ms", System.currentTimeMillis());
        broadcaster.publish(objectMapper.writeValueAsString(root));
    }

    public void publishRaw(String debeziumJson) {
        broadcaster.publish(debeziumJson);
    }
}
