package com.sparta.reader;

import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoDailyStatisticReader")
public class VideoDailyStatisticReader {
    @Bean
    @Scope("step")
    public JpaPagingItemReader<VideoDailyStatisticsEntity> dailyStatisticsReader(EntityManagerFactory entityManagerFactory) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        return new JpaPagingItemReaderBuilder<VideoDailyStatisticsEntity>()
                .name("videoDailyStatisticsReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM video_daily_statistics v WHERE v.date = :today")
                .parameterValues(Map.of("today", today))
                .pageSize(100)  // 한번에 읽을 항목 수
                .build();
    }

}
