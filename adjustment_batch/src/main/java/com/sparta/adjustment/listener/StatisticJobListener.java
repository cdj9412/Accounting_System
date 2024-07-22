package com.sparta.adjustment.listener;

import com.sparta.adjustment.exception.JobLogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;

@Slf4j
@RequiredArgsConstructor
public class StatisticJobListener implements JobExecutionListener, StepExecutionListener {

    private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	private final ThreadLocal<Long> stepStartTime = new ThreadLocal<>();

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
			JobLogMessage.CACHE_CLEARED.log(log);
		} else {
			JobLogMessage.JOB_INCOMPLETE.log(log);
			restartFailedJob(jobExecution);
		}
	}

	private void restartFailedJob(JobExecution failedJobExecution) {
		JobLogMessage.JOB_RESTARTING.log(log, failedJobExecution.getJobInstance().getJobName());

		JobParameters newJobParameters = createRestartParameters(failedJobExecution);

		try {
			Job job = jobRegistry.getJob("videoStatisticsJob");
			JobExecution restartedJobExecution = jobLauncher.run(job, newJobParameters);
			log.info("Restarted job execution status: {}", restartedJobExecution.getStatus());
		} catch (Exception e) {
			JobLogMessage.RESTART_FAILED.log(log, failedJobExecution.getJobInstance().getJobName(), e.getMessage());
		}
	}

	private JobParameters createRestartParameters(JobExecution failedJobExecution) {
		JobParametersBuilder parametersBuilder = new JobParametersBuilder(failedJobExecution.getJobParameters());

		// 재시작 시간을 파라미터로 추가
		parametersBuilder.addLong("restartTime", System.currentTimeMillis());

		// 실패한 스텝 이름을 파라미터로 추가
		failedJobExecution.getStepExecutions().stream()
			.filter(stepExecution -> stepExecution.getStatus() == BatchStatus.FAILED)
			.findFirst()
			.ifPresent(failedStep ->
				parametersBuilder.addString("failedStep", failedStep.getStepName()));

		return parametersBuilder.toJobParameters();
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		stepStartTime.set(System.currentTimeMillis());
		JobLogMessage.STEP_STARTING.log(log, stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		long duration = System.currentTimeMillis() - stepStartTime.get();
		stepStartTime.remove();

		logStepCompletion(stepExecution, duration);

		if (stepExecution.getStatus() == BatchStatus.FAILED) {
			return ExitStatus.FAILED;
		} else {
			return ExitStatus.COMPLETED;
		}
	}

	private void logStepCompletion(StepExecution stepExecution, long duration) {
		String status = stepExecution.getStatus() == BatchStatus.FAILED ? "failed" : "completed";
		log.info("Step {} {}. Duration: {} ms, Read count: {}, Write count: {}, Commit count: {}",
			stepExecution.getStepName(), status, duration, stepExecution.getReadCount(),
			stepExecution.getWriteCount(), stepExecution.getCommitCount());
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		JobLogMessage.JOB_STARTING.log(log, jobExecution.getJobInstance().getJobName());
	}
}