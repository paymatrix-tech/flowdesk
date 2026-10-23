package com.flowdesk.api.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/demo/payments")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "flowdesk.demo.payments.enabled", havingValue = "true")
public class DemoPaymentController {

    private final DemoPaymentService demoPaymentService;

    @PostMapping("/flush")
    public DemoFlushResult flush(@RequestParam(defaultValue = "0") int seed) {
        if (seed <= 0) {
            return demoPaymentService.flushAll();
        }
        return demoPaymentService.flushAndSeed(seed);
    }

    @PostMapping("/burst")
    public DemoBurstResult burst(@Valid @RequestBody BurstRequest request) {
        return demoPaymentService.runBurst(
                request.durationSeconds(),
                request.intervalMs(),
                request.notifyLive()
        );
    }

    public record BurstRequest(
            @Min(1) @Max(120) int durationSeconds,
            @Min(0) @Max(5000) int intervalMs,
            boolean notifyLive
    ) {
        public BurstRequest {
            if (durationSeconds <= 0) {
                durationSeconds = 5;
            }
            if (intervalMs < 0) {
                intervalMs = 0;
            }
        }
    }
}
