package com.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AsyncConfig - Configures asynchronous task execution for email sending
 * This ensures email operations don't block the main authentication flow
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * Configure thread pool for async email operations
     * Core pool: 2 threads
     * Max pool: 5 threads
     * Queue capacity: 100
     * Thread name prefix: farmflex-async-
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("farmflex-async-");
        executor.setAwaitTerminationSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        
        log.info("Async task executor initialized with core pool size: 2, max pool size: 5");
        
        return executor;
    }
}
