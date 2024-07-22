package com.sparta.adjustment.listener;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsStepExecutionListener implements StepExecutionListener {

	private final MeterRegistry meterRegistry;
	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
	private LocalDateTime startTime;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		startTime = LocalDateTime.now();
		recordThreadPoolMetrics(stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		LocalDateTime endTime = LocalDateTime.now();
		Duration duration = Duration.between(startTime, endTime);

		Timer.builder("custom_batch_step_duration_seconds")
			.tag("step", stepExecution.getStepName())
			.tag("status", stepExecution.getExitStatus().getExitCode())
			.register(meterRegistry)
			.record(duration);

		recordThreadPoolMetrics(stepExecution.getStepName());

		return stepExecution.getExitStatus();
	}

	private void recordThreadPoolMetrics(String stepName) {
		ThreadPoolExecutor executor = threadPoolTaskExecutor.getThreadPoolExecutor();

		recordGauge("custom_batch_thread_pool_size_total", stepName, executor.getPoolSize());
		recordGauge("custom_batch_thread_pool_active_threads_total", stepName, executor.getActiveCount());
		recordGauge("custom_batch_thread_pool_max_size_total", stepName, executor.getMaximumPoolSize());
		recordGauge("custom_batch_thread_pool_queue_size_total", stepName, executor.getQueue().size());
		recordCounter("custom_batch_thread_pool_completed_tasks_total", stepName, executor.getCompletedTaskCount());
		recordCounter("custom_batch_thread_pool_total_tasks_total", stepName, executor.getTaskCount());

		log.info("Thread Pool Metrics for step {}: Size: {}, Active: {}, Max: {}, Queue: {}, Completed Tasks: {}, Total Tasks: {}",
			stepName, executor.getPoolSize(), executor.getActiveCount(), executor.getMaximumPoolSize(),
			executor.getQueue().size(), executor.getCompletedTaskCount(), executor.getTaskCount());
	}

	private void recordGauge(String metricName, String stepName, Number value) {
		meterRegistry.gauge(metricName, List.of(Tag.of("step", stepName)), value);
	}

	private void recordCounter(String metricName, String stepName, Number value) {
		meterRegistry.counter(metricName, "step", stepName).increment(value.doubleValue());
	}
}