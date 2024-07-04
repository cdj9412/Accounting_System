package com.sparta.reader;

import com.sparta.entity.VideoAdDailyViewsEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoAdDailyViewsReader")
public class VideoAdDailyViewsReader {
    @Bean
    @Scope("step")
    public JpaPagingItemReader<VideoAdDailyViewsEntity> videoAdDailyViewsEntityReader(EntityManagerFactory entityManagerFactory) {
        // 이전 날짜나 빠진 정산 부분에 대해서 처리하는 로직이 들어가야 하지만 일단 오늘 것만.

        return new JpaPagingItemReaderBuilder<VideoAdDailyViewsEntity>()
                .name("videoAdDailyViewsReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM video_ad_daily_views v WHERE v.date = CURRENT_DATE")
                .pageSize(100)  // 한번에 읽을 항목 수
                .build();
    }
}
