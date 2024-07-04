package com.sparta.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "JobCompletionNotificationListener")
public class JobCompletionNotificationListener implements JobExecutionListener {
    @Override
    public void afterJob(JobExecution jobExecution) {
        // 작업이 실패한 경우 경고 로그 출력
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.warn("Job failed with status: {}", jobExecution.getStatus());
        } else {
            // 작업이 성공적으로 완료된 경우 정보 로그 출력
            log.info("Job completed successfully with status: {}", jobExecution.getStatus());
        }
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // 작업 시작 전에 호출될 로직 (필요에 따라 구현)
    }
}
