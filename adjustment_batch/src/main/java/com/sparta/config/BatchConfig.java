package com.sparta.config;

import com.sparta.entity.VideoAdDailyViewsEntity;
import com.sparta.entity.VideoDailySettlementEntity;
import com.sparta.entity.VideoDailyStatisticsEntity;
import com.sparta.entity.VideoDailyViewsEntity;
import com.sparta.listener.JobCompletionNotificationListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "BatchConfig")
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 일간 통계 Job 정의
    @Bean
    public Job dailyStatisticsJob(JobCompletionNotificationListener listener, Step dailyStatisticsStep) {
        log.info("일일 통계 job 실행...");
        return new JobBuilder("dailyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(dailyStatisticsStep)
                .build();
    }

    // 일간 통계 Step 정의
    @Bean
    public Step dailyStatisticsStep(
            JpaPagingItemReader<VideoDailyViewsEntity> reader,
            ItemProcessor<VideoDailyViewsEntity, VideoDailyStatisticsEntity> processor,
            ItemWriter<VideoDailyStatisticsEntity> writer,
            TaskExecutor statsThreadPoolTaskExecutor ) {
        log.info("일일 통계 step 정의...");
        return new StepBuilder("dailyStatisticsStep", jobRepository)
                .<VideoDailyViewsEntity, VideoDailyStatisticsEntity>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(statsThreadPoolTaskExecutor)
                .build();
    }

    @Bean
    public TaskExecutor statsThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 기본적으로 유지되는 스레드 수
        executor.setMaxPoolSize(10); // 최대로 확장 가능한 스레드 수
        executor.setQueueCapacity(25); // 작업이 대기할 수 있는 최대 수
        executor.setThreadNamePrefix("statistics-thread-"); // 생성되는 스레드의 이름 접두사
        executor.initialize();
        log.info("Initialized statsThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }

    /************************************ 정산 ************************************/

    // 일간 정산 Job 정의
    @Bean
    public Job dailySettlementJob(JobCompletionNotificationListener listener,
                                  Step videoDailyViewsStep,
                                  Step videoAdDailyViewsStep) {
        log.info("일간 정산 Job 실행...");
        return new JobBuilder("dailySettlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(videoDailyViewsStep)
                .next(videoAdDailyViewsStep)
                .build();
    }

    // 일간 조회수 정산 Step 정의
    @Bean
    public Step videoDailyViewsStep(
            JpaPagingItemReader<VideoDailyViewsEntity> reader,
            ItemProcessor<VideoDailyViewsEntity, VideoDailySettlementEntity> viewsProcessor,
            @Qualifier("videoDailySettlementWriterFirst") ItemWriter<VideoDailySettlementEntity> writer,
            TaskExecutor settleThreadPoolTaskExecutor) {
        log.info("일간 조회수 정산 단계 정의...");
        return new StepBuilder("videoDailyViewsStep", jobRepository)
                .<VideoDailyViewsEntity, VideoDailySettlementEntity>chunk(10, transactionManager)
                .reader(reader)
                .processor(viewsProcessor)
                .writer(writer)
                .taskExecutor(settleThreadPoolTaskExecutor)
                .build();
    }

    // 일간 광고 조회수 정산 Step 정의
    @Bean
    public Step videoAdDailyViewsStep(
            JpaPagingItemReader<VideoAdDailyViewsEntity> reader,
            ItemProcessor<VideoAdDailyViewsEntity, VideoDailySettlementEntity> adViewsProcessor,
            @Qualifier("videoDailySettlementWriterSecond") ItemWriter<VideoDailySettlementEntity> writer,
            TaskExecutor settleThreadPoolTaskExecutor) {
        log.info("일간 광고 조회수 정산 단계 정의...");
        return new StepBuilder("videoAdDailyViewsStep", jobRepository)
                .<VideoAdDailyViewsEntity, VideoDailySettlementEntity>chunk(100, transactionManager)
                .reader(reader)
                .processor(adViewsProcessor)
                .writer(writer)
                .taskExecutor(settleThreadPoolTaskExecutor)
                .build();
    }

    @Bean
    public TaskExecutor settleThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("settlement-thread-");
        executor.initialize();
        log.info("Initialized settleThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }
}
