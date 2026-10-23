package com.flowdesk.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class CdcAsyncConfig {

    @Bean(name = "cdcTaskExecutor")
    TaskExecutor cdcTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(2_000);
        executor.setThreadNamePrefix("cdc-async-");
        executor.initialize();
        return executor;
    }
}
