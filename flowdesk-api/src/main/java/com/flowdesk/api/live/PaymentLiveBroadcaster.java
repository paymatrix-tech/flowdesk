package com.flowdesk.api.live;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentLiveBroadcaster {

    private static final long EMITTER_TIMEOUT_MS = TimeUnit.HOURS.toMillis(12);

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("sse-live-");

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        taskExecutor.execute(() -> sendConnected(emitter));
        return emitter;
    }

    private void sendConnected(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            log.debug("SSE client disconnected before connected event", e);
            emitters.remove(emitter);
        }
    }

    public void publish(String payload) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("payment").data(payload));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
