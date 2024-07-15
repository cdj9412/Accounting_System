package com.sparta.reader;

import com.sparta.entity.VideoAdDailyViewsEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoAdDailyViewsReader")
public class VideoAdDailyViewsReader {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaPagingItemReader<VideoAdDailyViewsEntity> videoAdDailyViewsEntityReader(
            @Value("#{jobParameters['chunkSize']}") int chunkSize) {

        log.info("현재 reader thread 이름 : {}", Thread.currentThread().getName());

        LocalDate yesterday = LocalDate.now().minusDays(1); //어제 날짜

        return new JpaPagingItemReaderBuilder<VideoAdDailyViewsEntity>()
                .name("videoAdDailyViewsReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM video_ad_daily_views v WHERE v.date = :yesterday") // 어제 날짜
                .parameterValues(Collections.singletonMap("yesterday", yesterday))
                .pageSize(chunkSize)  // jobParameters 로부터 가져온 chunk 크기로 설정 한번에 읽을 항목 수
                .saveState(false)     // 멀티스레드 환경에서 안전하게 동작하도록 상태 저장 비활성화
                .build();
    }
}
