package com.sparta.adjustment.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;

public class MetricsConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(@Value("${spring.application.name}") String appName) {
        return registry -> registry.config().commonTags("application", appName, "instance", UUID.randomUUID().toString());
    }

    @Bean
    public MeterBinder threadPoolTaskExecutorMetrics(ThreadPoolTaskExecutor taskExecutor) {
        return new ExecutorServiceMetrics(taskExecutor.getThreadPoolExecutor(),
                "adjustmentThreadPool", Tags.empty());
    }
}
