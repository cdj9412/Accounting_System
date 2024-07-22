package com.sparta.adjustment.config;

import com.sparta.adjustment.entity.VideoDailySettlementEntity;
import com.sparta.adjustment.entity.VideoDailyStatisticsEntity;
import com.sparta.adjustment.listener.MetricsStepExecutionListener;
import com.sparta.adjustment.listener.ThreadPoolMonitoringListener;
import com.sparta.adjustment.validator.UniqueJobParametersValidator;
import com.sparta.partitioner.StatisticsPartitioner;
import com.sparta.partitioner.ViewPartitioner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "BatchConfig")
public class BatchConfig extends DefaultBatchConfiguration {

    @Override
    protected Isolation getIsolationLevelForCreate() {
        return Isolation.READ_COMMITTED;
    }

    @Override
    protected DataSource getDataSource() {
        return super.getDataSource();
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return super.getTransactionManager();
    }

    @Bean
    @StepScope
    public ViewPartitioner viewPartitioner(
            @Value("#{jobParameters['date']}") LocalDate date,
            @Value("${batch.gridSize:10}") int gridSize) {
        return new ViewPartitioner(getDataSource(), gridSize, date);
    }

    @Bean
    @StepScope
    public StatisticsPartitioner adViewPartitioner(
            @Value("#{jobParameters['date']}") LocalDate date,
            @Value("${batch.gridSize:10}") int gridSize) {
        return new StatisticsPartitioner(getDataSource(), gridSize, date);
    }

    // 일간 통계 Job 정의
    @Bean
    public Job dailyStatisticsJob(
            JobRepository jobRepository,
            ThreadPoolMonitoringListener threadPoolMonitoringListener,
            Step partitionedDailyStatisticsStep) {
        log.info("일일 통계 job 실행...");
        return new JobBuilder("dailyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(partitionedDailyStatisticsStep)
                .validator(new UniqueJobParametersValidator())
                .listener(threadPoolMonitoringListener)
                .build();
    }

    @Bean
    public Step partitionedDailyStatisticsStep(
            JobRepository jobRepository,
            Step dailyStatisticsStep,
            Partitioner viewPartitioner,
            @Value("${batch.gridSize:10}") int gridSize) {
        return new StepBuilder("partitionedDailyStatisticsStep", jobRepository)
                .partitioner("dailyStatisticsStep", viewPartitioner)
                .step(dailyStatisticsStep)
                .gridSize(gridSize)
                .taskExecutor(threadPoolTaskExecutor())
                .build();
    }

    // 일간 통계 Step 정의
    // processor 삭제
    @Bean
    public Step dailyStatisticsStep(
            JobRepository jobRepository,
            @Qualifier("videoDailyStatisticsReader")JdbcPagingItemReader<VideoDailyStatisticsEntity> reader,
            ItemWriter<VideoDailyStatisticsEntity> writer,
            MetricsStepExecutionListener metricsStepExecutionListener,
            @Value("${batch.chunkSize:2000}") int chunkSize ) {
        log.info("일일 통계 step 정의...");
        return new StepBuilder("dailyStatisticsStep", jobRepository)
                .<VideoDailyStatisticsEntity, VideoDailyStatisticsEntity>chunk(chunkSize, getTransactionManager())
                .reader(reader)
                .writer(writer)
                .listener(metricsStepExecutionListener)
                .build();
    }

    /************************************ 정산 ************************************/

    // 일간 정산 Job 정의
    @Bean
    public Job dailySettlementJob(
            JobRepository jobRepository,
            ThreadPoolMonitoringListener threadPoolMonitoringListener,
            Step partitionedDailySettlementStep) {
        log.info("일간 정산 Job 실행...");
        return new JobBuilder("dailySettlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(partitionedDailySettlementStep)
                .validator(new UniqueJobParametersValidator())
                .listener(threadPoolMonitoringListener)
                .build();
    }

    @Bean
    public Step partitionedDailySettlementStep(
            JobRepository jobRepository,
            Step videoSettlementStep,
            Partitioner viewPartitioner,
            @Value("${batch.gridSize:10}") int gridSize) {
        return new StepBuilder("partitionedDailyViewsStep", jobRepository)
                .partitioner("videoDailyViewsStep", viewPartitioner)
                .step(videoSettlementStep)
                .gridSize(gridSize)
                .taskExecutor(threadPoolTaskExecutor())
                .build();
    }

    // 일간 정산 Step 정의
    @Bean
    public Step videoSettlementStep(
            JobRepository jobRepository,
            @Qualifier("videoDailySettlementReader") JdbcPagingItemReader<VideoDailyStatisticsEntity> reader,
            ItemProcessor<VideoDailyStatisticsEntity, VideoDailySettlementEntity> processor,
            ItemWriter<VideoDailySettlementEntity> writer,
            MetricsStepExecutionListener metricsStepExecutionListener,
            @Value("${batch.chunkSize:2000}") int chunkSize) {
        log.info("일간 정산 step 정의...");
        return new StepBuilder("videoDailyViewsStep", jobRepository)
                .<VideoDailyStatisticsEntity, VideoDailySettlementEntity>chunk(chunkSize, getTransactionManager())
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(metricsStepExecutionListener)
                .build();
    }

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 기본적으로 유지되는 스레드 수
        executor.setMaxPoolSize(16); // 최대로 확장 가능한 스레드 수
        executor.setQueueCapacity(25); // 작업이 대기할 수 있는 최대 수
        executor.setThreadNamePrefix("adjustment-thread-"); // 생성되는 스레드의 이름 접두사
        executor.initialize();
        log.info("Initialized ThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }
}
