package com.sparta.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j(topic = "SchedulingConfig")
public class SchedulingConfig {
    private final JobLauncher jobLauncher;
    private final Job dailyStatisticsJob;
    private final Job dailySettlementJob;

    //@Scheduled(cron = "초 분 시 일 월 요일(무관:?)")

    // 배치 작업을 한국시간 1:10 에 트리거하여 스케줄러 구성
    //@Scheduled(cron = "0 10 1 * * ?", zone = "Asia/Seoul")
    @Scheduled(fixedRate = 200000) // 200초마다 실행 test
    public void runJobsSequentially() {
        try {
            // 통계 작업 스케줄링
            log.info("통계 Job 시작...");
            JobParameters statisticsJobParameters  = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLong("chunkSize", 100L)
                    .addString("uniqueId", UUID.randomUUID().toString())
                    .toJobParameters();

            // 일간 통계 Job 실행
            log.info("일간 통계 Job 시작...");
            jobLauncher.run(dailyStatisticsJob, statisticsJobParameters);
            log.info("일간 통계 Job 종료.");

            // 일간 통계 작업이 완료된 후 정산 작업 실행
            log.info("정산 Job 시작...");
            JobParameters settlementJobParameters  = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLong("chunkSize", 100L)
                    .addString("uniqueId", UUID.randomUUID().toString())
                    .toJobParameters();

            // 일간 정산 Job 실행
            log.info("일간 정산 Job 시작...");
            jobLauncher.run(dailySettlementJob, settlementJobParameters);
            log.info("일간 정산 Job 종료.");

            log.info("Job 종료.");
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
