package com.rtsp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AppConfig {
    @Value("${task.executor.core.pool.size}")
    private int corePoolSize;

    @Value("${task.executor.max.pool.size}")
    private int maxPoolSize;

    @Value("${task.executor.queue.capacity}")
    private int queueCapacity;

    // Thread Pool 설정
    @Bean
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Estimation-");
        executor.initialize();

        return executor;
    }
}
