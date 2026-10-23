package com.flowdesk.api.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flowdesk.demo.payments")
public record DemoPaymentProperties(
        boolean enabled,
        int maxBurstSeconds,
        int minIntervalMs
) {
    public DemoPaymentProperties {
        if (maxBurstSeconds <= 0) {
            maxBurstSeconds = 30;
        }
        if (minIntervalMs < 0) {
            minIntervalMs = 0;
        }
    }
}
