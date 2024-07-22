package com.sparta.adjustment.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
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
    @Scheduled(fixedRate = 3600000) // 3600초마다 실행 test
    public void runJobsSequentially() {
        try {
            // 통계 작업 스케줄링
            log.info("통계 Job 시작...");
            long statisticsJobStartTime = System.currentTimeMillis();
            JobParameters statisticsJobParameters  = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLocalDate("date", LocalDate.of(2024, 7, 16))
                    .addLong("chunkSize", 2000L)
                    .addString("uniqueId", UUID.randomUUID().toString())
                    .toJobParameters();

            // 일간 통계 Job 실행
            log.info("일간 통계 Job 시작...");
            jobLauncher.run(dailyStatisticsJob, statisticsJobParameters);
            long statisticsJobEndTime = System.currentTimeMillis();
            log.info("일간 통계 Job 종료.");

            log.info("일간 통계 Job 시작 시간: {}", statisticsJobStartTime);
            log.info("일간 통계 Job 종료 시간: {}", statisticsJobEndTime);
            log.info("일간 통계 Job 소요 시간: {} ms", (statisticsJobEndTime - statisticsJobStartTime));

            // 일간 통계 작업이 완료된 후 정산 작업 실행
            log.info("정산 Job 시작...");
            long settlementJobStartTime = System.currentTimeMillis();
            JobParameters settlementJobParameters  = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLocalDate("date", LocalDate.of(2024, 7, 16))
                    .addLong("chunkSize", 1000L)
                    .addString("uniqueId", UUID.randomUUID().toString())
                    .toJobParameters();

            // 일간 정산 Job 실행
            log.info("일간 정산 Job 시작...");
            jobLauncher.run(dailySettlementJob, settlementJobParameters);
            long settlementJobEndTime = System.currentTimeMillis();
            log.info("일간 정산 Job 종료.");

            log.info("일간 정산 Job 시작 시간: {}", settlementJobStartTime);
            log.info("일간 정산 Job 종료 시간: {}", settlementJobEndTime);
            log.info("일간 정산 Job 소요 시간: {} ms", (settlementJobEndTime - settlementJobStartTime));

            log.info("Job 종료.");
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
