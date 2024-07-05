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

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j(topic = "SchedulingConfig")
public class SchedulingConfig {
    private final JobLauncher jobLauncher;
    private final Job dailyStatisticsJob;
    private final Job dailySettlementJob;

    //@Scheduled(cron = "초 분 시 일 월 요일(무관:?)")

    // 배치 작업을 한국시간 6:00, 12:00, 18:00, 자정에 트리거하여 스케줄러 구성
    @Scheduled(cron = "0 0 6,12,18,0 * * ?", zone = "Asia/Seoul")
    //@Scheduled(cron = "0/10 * * * * *") // test
    public void runStatisticsJob() throws Exception {
        // 통계 작업 스케줄링
        log.info("통계 Job 시작...");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // 일간 통계 Job 실행
        log.info("일간 통계 Job 시작...");
        jobLauncher.run(dailyStatisticsJob, jobParameters);
        log.info("일간 통계 Job 종료.");

        log.info("통계 Job 종료.");
    }

    // 배치 작업을 한국시간 6:20, 12:20, 18:20, 00:20에 트리거하여 스케줄러 구성
    @Scheduled(cron = "0 20 6,12,18,0 * * ?", zone = "Asia/Seoul")
    //@Scheduled(cron = "0/20 * * * * *") //test
    public void runSettlementJob() throws Exception {
        // 정산 작업 스케줄링
        log.info("정산 Job 시작...");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // 일간 정산 Job 실행
        log.info("일간 정산 Job 시작...");
        jobLauncher.run(dailySettlementJob, jobParameters);
        log.info("일간 정산 Job 종료.");

        log.info("정산 Job 종료.");
    }
}
