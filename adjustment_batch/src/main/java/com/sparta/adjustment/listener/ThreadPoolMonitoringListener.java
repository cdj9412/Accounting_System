package com.sparta.adjustment.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ThreadPoolMonitoringListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(ThreadPoolMonitoringListener.class);
	private final ThreadPoolTaskExecutor taskExecutor;
	private volatile boolean isJobRunning = false;
	private final AtomicInteger lastActiveCount = new AtomicInteger(0);
	private final AtomicInteger lastCompletedTaskCount = new AtomicInteger(0);
	private final AtomicInteger lastQueueSize = new AtomicInteger(0);

	public ThreadPoolMonitoringListener(@Qualifier("threadPoolTaskExecutor")ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		isJobRunning = true;
		startMonitoring();
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		isJobRunning = false;
		// 작업 종료 시 최종 상태 로깅
		logThreadPoolStatus();
	}

	private void startMonitoring() {
		new Thread(() -> {
			while (isJobRunning) {
				checkAndLogThreadPoolStatus();
				try {
					Thread.sleep(100); // 100ms 간격으로 체크
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}).start();
	}

	private void checkAndLogThreadPoolStatus() {
		if (taskExecutor == null) {
			log.warn("TaskExecutor is not an instance of ThreadPoolTaskExecutor. Monitoring is not possible.");
			return;
		}

		ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor;
		int currentActiveCount = executor.getActiveCount();
		int currentCompletedTaskCount = (int) executor.getThreadPoolExecutor().getCompletedTaskCount();
		int currentQueueSize = executor.getThreadPoolExecutor().getQueue().size();

		boolean hasChanged = false;

		if (currentActiveCount != lastActiveCount.get()) {
			lastActiveCount.set(currentActiveCount);
			hasChanged = true;
		}
		if (currentCompletedTaskCount != lastCompletedTaskCount.get()) {
			lastCompletedTaskCount.set(currentCompletedTaskCount);
			hasChanged = true;
		}
		if (currentQueueSize != lastQueueSize.get()) {
			lastQueueSize.set(currentQueueSize);
			hasChanged = true;
		}

		if (hasChanged) {
			logThreadPoolStatus();
		}
	}

	private void logThreadPoolStatus() {
		log.info("Thread pool status changed: " +
				"Active threads: {}, " +
				"Completed tasks: {}, " +
				"Task queue size: {}",
			lastActiveCount.get(),
			lastCompletedTaskCount.get(),
			lastQueueSize.get());
	}
}