package com.flowdesk.api.demo;

public record DemoFlushResult(long paymentsRemovedFromDb, boolean elasticsearchIndexCleared) {}
