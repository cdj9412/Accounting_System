package com.sparta.config;

import com.sparta.entity.VideoAdDailyViewsEntity;
import com.sparta.entity.VideoDailySettlementEntity;
import com.sparta.entity.VideoDailyStatisticsEntity;
import com.sparta.entity.VideoDailyViewsEntity;
import com.sparta.listener.JobCompletionNotificationListener;
import com.sparta.reader.VideoAdDailyViewsReader;
import com.sparta.reader.VideoDailyViewsReader;
import com.sparta.writer.VideoDailySettlementWriter;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "BatchConfig")
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    // 일간 조회수/광고조회수 reader custom
    private final VideoDailyViewsReader videoDailyViewsReader;
    private final VideoAdDailyViewsReader videoAdDailyViewsReader;

    // 일간 정산 writer custom
    private final VideoDailySettlementWriter videoDailySettlementWriter;

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
}
