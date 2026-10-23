package com.flowdesk.api.search;

import java.util.Map;

import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Chooses query field names for the active {@code payments} index mapping.
 * Spring Data auto-mapping uses {@code text} + {@code .keyword}; Flowdesk JSON mappings use plain {@code keyword}.
 */
@Slf4j
@Component
public class PaymentEsFieldResolver {

    @Getter
    private volatile String paymentStatusField = PaymentEsQueryFields.PAYMENT_STATUS;
    @Getter
    private volatile String memopostStatusField = PaymentEsQueryFields.MEMOPOST_STATUS;
    @Getter
    private volatile String paymentIdField = PaymentEsQueryFields.PAYMENT_ID;

    public void refreshFromIndex(IndexOperations indexOps) {
        if (!indexOps.exists()) {
            resetToCanonical();
            return;
        }
        try {
            Map<String, Object> mapping = indexOps.getMapping();
            Map<String, Object> properties = resolveProperties(mapping);
            if (properties == null) {
                return;
            }
            paymentStatusField = resolveKeywordField(properties, PaymentEsQueryFields.PAYMENT_STATUS);
            memopostStatusField = resolveKeywordField(properties, PaymentEsQueryFields.MEMOPOST_STATUS);
            paymentIdField = resolveKeywordField(properties, PaymentEsQueryFields.PAYMENT_ID);
            log.info(
                    "Elasticsearch payments query fields: paymentStatus={}, memopostStatus={}, paymentId={}",
                    paymentStatusField,
                    memopostStatusField,
                    paymentIdField);
        } catch (Exception e) {
            log.debug("Could not resolve payments index fields, using defaults: {}", e.getMessage());
        }
    }

    private void resetToCanonical() {
        paymentStatusField = PaymentEsQueryFields.PAYMENT_STATUS;
        memopostStatusField = PaymentEsQueryFields.MEMOPOST_STATUS;
        paymentIdField = PaymentEsQueryFields.PAYMENT_ID;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> resolveProperties(Map<String, Object> mapping) {
        if (mapping == null || mapping.isEmpty()) {
            return null;
        }
        if (mapping.containsKey("properties")) {
            return (Map<String, Object>) mapping.get("properties");
        }
        for (Object value : mapping.values()) {
            if (value instanceof Map<?, ?> nested) {
                Object props = nested.get("properties");
                if (props instanceof Map<?, ?> propMap) {
                    return (Map<String, Object>) propMap;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static String resolveKeywordField(Map<String, Object> properties, String fieldName) {
        Object field = properties.get(fieldName);
        if (!(field instanceof Map<?, ?> fieldMap)) {
            return fieldName;
        }
        if ("keyword".equals(String.valueOf(fieldMap.get("type")))) {
            return fieldName;
        }
        if ("text".equals(fieldMap.get("type"))) {
            Object fields = fieldMap.get("fields");
            if (fields instanceof Map<?, ?> subfields && subfields.containsKey("keyword")) {
                return fieldName + ".keyword";
            }
        }
        return fieldName;
    }
}
