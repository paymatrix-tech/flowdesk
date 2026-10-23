package com.flowdesk.api.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DemoPaymentProperties.class)
@ConditionalOnProperty(name = "flowdesk.demo.payments.enabled", havingValue = "true")
public class DemoPaymentConfiguration {
}
