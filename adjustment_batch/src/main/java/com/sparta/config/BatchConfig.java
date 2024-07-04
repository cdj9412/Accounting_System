package com.sparta.config;

import com.sparta.entity.VideoAdDailyViewsEntity;
import com.sparta.entity.VideoDailyViewsEntity;
import com.sparta.entity.daily.VideoDailySettlementEntity;
import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import com.sparta.entity.monthly.VideoMonthlySettlementEntity;
import com.sparta.entity.monthly.VideoMonthlyStatisticsEntity;
import com.sparta.entity.weekly.VideoWeeklySettlementEntity;
import com.sparta.entity.weekly.VideoWeeklyStatisticsEntity;
import com.sparta.listener.JobCompletionNotificationListener;
import com.sparta.reader.VideoAdDailyViewsReader;
import com.sparta.reader.VideoDailySettlementReader;
import com.sparta.reader.VideoDailyStatisticReader;
import com.sparta.reader.VideoDailyViewsReader;
import com.sparta.writer.daily.VideoDailySettlementWriter;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
@Slf4j(topic = "BatchConfig")
public class BatchConfig {

    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;
    private EntityManagerFactory entityManagerFactory;

    // 일간 조회수/광고조회수 reader custom
    private VideoDailyViewsReader videoDailyViewsReader;
    private VideoAdDailyViewsReader videoAdDailyViewsReader;

    // 일간 정산 writer custom
    private VideoDailySettlementWriter videoDailySettlementWriter;

    // 통계 작성을 위한 일간 통계 reader custom
    private VideoDailyStatisticReader videoDailyStatisticReader;

    // 정산 작성을 위한 일간 정산 reader custom
    private VideoDailySettlementReader videoDailySettlementReader;

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
    public Step dailyStatisticsStep(ItemProcessor<VideoDailyViewsEntity, VideoDailyStatisticsEntity> processor,
                                    ItemWriter<VideoDailyStatisticsEntity> writer) {
        log.info("일일 통계 step 정의...");
        return new StepBuilder("dailyStatisticsStep", jobRepository)
                .<VideoDailyViewsEntity, VideoDailyStatisticsEntity>chunk(10, transactionManager)
                .reader(videoDailyViewsReader.videoDailyViewsEntityReader(entityManagerFactory))
                .processor(processor)
                .writer(writer)
                .build();
    }

    // 주간 통계 Job 정의
    @Bean
    public Job weeklyStatisticsJob(JobCompletionNotificationListener listener, Step weeklyStatisticsStep) {
        log.info("주간 통계 job 실행...");
        return new JobBuilder("weeklyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(weeklyStatisticsStep)
                .build();
    }

    // 주간 통계 Step 정의
    @Bean
    public Step weeklyStatisticsStep(ItemProcessor<VideoDailyStatisticsEntity, VideoWeeklyStatisticsEntity> processor,
                                     ItemWriter<VideoWeeklyStatisticsEntity> writer) {
        log.info("주간 통계 step 정의...");
        return new StepBuilder("weeklyStatisticsStep", jobRepository)
                .<VideoDailyStatisticsEntity, VideoWeeklyStatisticsEntity>chunk(10, transactionManager)
                .reader(videoDailyStatisticReader.dailyStatisticsReader(entityManagerFactory))
                .processor(processor)
                .writer(writer)
                .build();
    }

    // 월간 통계 Job 정의
    @Bean
    public Job monthlyStatisticsJob(JobCompletionNotificationListener listener, Step monthlyStatisticsStep) {
        log.info("월간 통계 job 실행...");
        return new JobBuilder("monthlyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(monthlyStatisticsStep)
                .build();
    }

    // 월간 통계 Step 정의
    @Bean
    public Step monthlyStatisticsStep(ItemProcessor<VideoDailyStatisticsEntity, VideoMonthlyStatisticsEntity> processor,
                                     ItemWriter<VideoMonthlyStatisticsEntity> writer) {
        log.info("월간 통계 step 정의...");
        return new StepBuilder("monthlyStatisticsStep", jobRepository)
                .<VideoDailyStatisticsEntity, VideoMonthlyStatisticsEntity>chunk(10, transactionManager)
                .reader(videoDailyStatisticReader.dailyStatisticsReader(entityManagerFactory))
                .processor(processor)
                .writer(writer)
                .build();
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
    public Step videoDailyViewsStep(ItemProcessor<VideoDailyViewsEntity, VideoDailySettlementEntity> viewsProcessor) {
        log.info("일간 조회수 정산 단계 정의...");
        return new StepBuilder("videoDailyViewsStep", jobRepository)
                .<VideoDailyViewsEntity, VideoDailySettlementEntity>chunk(10, transactionManager)
                .reader(videoDailyViewsReader.videoDailyViewsEntityReader(entityManagerFactory))
                .processor(viewsProcessor)
                .writer(items -> videoDailySettlementWriter.videoDailySettlementWriterFirst(items.getItems()))
                .build();
    }

    // 일간 광고 조회수 정산 Step 정의
    @Bean
    public Step videoAdDailyViewsStep(ItemProcessor<VideoAdDailyViewsEntity, VideoDailySettlementEntity> adViewsProcessor) {
        log.info("일간 광고 조회수 정산 단계 정의...");
        return new StepBuilder("videoAdDailyViewsStep", jobRepository)
                .<VideoAdDailyViewsEntity, VideoDailySettlementEntity>chunk(100, transactionManager)
                .reader(videoAdDailyViewsReader.videoAdDailyViewsEntityReader(entityManagerFactory))
                .processor(adViewsProcessor)
                .writer(items -> videoDailySettlementWriter.videoDailySettlementWriterSecond(items.getItems()))
                .build();
    }

    // 주간 정산 Job 정의
    @Bean
    public Job weeklySettlementJob(JobCompletionNotificationListener listener,
                                  Step weeklySettlementStep) {
        log.info("주간 정산 Job 실행...");
        return new JobBuilder("weeklySettlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(weeklySettlementStep)
                .build();
    }

    // 주간 정산 Step 정의
    @Bean
    public Step weeklySettlementStep(ItemProcessor<VideoDailySettlementEntity, VideoWeeklySettlementEntity> processor,
                                     ItemWriter<VideoWeeklySettlementEntity> writer) {
        log.info("주간 정산 step 정의...");
        return new StepBuilder("weeklySettlementStep", jobRepository)
                .<VideoDailySettlementEntity, VideoWeeklySettlementEntity>chunk(10, transactionManager)
                .reader(videoDailySettlementReader.dailySettlementReader(entityManagerFactory))
                .processor(processor)
                .writer(writer)
                .build();
    }

    // 월간 정산 Job 정의
    @Bean
    public Job monthlySettlementJob(JobCompletionNotificationListener listener,
                                   Step monthlySettlementStep) {
        log.info("월간 정산 Job 실행...");
        return new JobBuilder("monthlySettlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(monthlySettlementStep)
                .build();
    }

    // 월간 정산 Step 정의
    @Bean
    public Step monthlySettlementStep(ItemProcessor<VideoDailySettlementEntity, VideoMonthlySettlementEntity> processor,
                                     ItemWriter<VideoMonthlySettlementEntity> writer) {
        log.info("월간 정산 step 정의...");
        return new StepBuilder("weeklySettlementStep", jobRepository)
                .<VideoDailySettlementEntity, VideoMonthlySettlementEntity>chunk(10, transactionManager)
                .reader(videoDailySettlementReader.dailySettlementReader(entityManagerFactory))
                .processor(processor)
                .writer(writer)
                .build();
    }
}
