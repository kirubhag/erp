package krs.erp.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for async task execution
 * Dedicated thread pool for student promotion processing
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * Thread pool executor for promotion tasks
     * Configured to handle bulk promotion operations efficiently
     * 
     * @return Executor for async promotion tasks
     */
    @Bean(name = "promotionTaskExecutor")
    public Executor promotionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("promotion-exec-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * Thread pool executor for activity logging
     * Configured to handle audit trail logging without impacting user operations
     * 
     * @return Executor for async activity logging
     */
    @Bean(name = "activityLogTaskExecutor")
    public Executor activityLogTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("activity-log-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
