package com.sparta.adjustment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
@Getter
public enum JobLogMessage {
	CACHE_CLEARED(LogLevel.INFO, JobStatus.COMPLETED, "job 완료 후 캐시가 지워졌습니다."),
	JOB_INCOMPLETE(LogLevel.ERROR, JobStatus.FAILED, "job 이 성공적으로 완료되지 않았습니다. 실패한 단계를 확인하십시오."),
	JOB_RESTARTING(LogLevel.ERROR, JobStatus.FAILED, "job 을 재시작 합니다"),
	STEP_FAILED(LogLevel.ERROR, JobStatus.FAILED, "step 실패: {}"),
	STEP_COMPLETED(LogLevel.INFO, JobStatus.COMPLETED, "step 이 성공적으로 완료되었습니다: {}"),
	JOB_STARTING(LogLevel.INFO, JobStatus.STARTING, "job 시작 중: {}"),
	STEP_STARTING(LogLevel.INFO, JobStatus.STARTING, "step 시작 중: {}"),
	STEP_RESTARTING(LogLevel.INFO, JobStatus.RESTARTING, "실패한 단계를 다시 시작 중: {}"),
	RESTART_FAILED(LogLevel.ERROR, JobStatus.FAILED, "재시작이 실패 했습니다");

	private final LogLevel logLevel;
	private final JobStatus status;
	private final String message;

	public void log(Logger logger, Object... args) {
		switch (logLevel) {
			case INFO -> logger.info(message, args);
			case WARN -> logger.warn(message, args);
			case ERROR -> logger.error(message, args);
			case DEBUG -> logger.debug(message, args);
		}
	}

	private enum LogLevel {
		INFO, WARN, ERROR, DEBUG
	}

	public enum JobStatus {
		STARTING, COMPLETED, FAILED, RESTARTING
	}
}
