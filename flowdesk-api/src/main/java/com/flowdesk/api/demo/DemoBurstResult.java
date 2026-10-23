package com.flowdesk.api.demo;

public record DemoBurstResult(int inserted, long elapsedMs, double writesPerSecond, boolean maxThroughput) {
}
