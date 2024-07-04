package com.sparta.reader;

import com.sparta.entity.daily.VideoDailySettlementEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoDailySettlementReader")
public class VideoDailySettlementReader {
    @Bean
    @Scope("step")
    public JpaPagingItemReader<VideoDailySettlementEntity> dailySettlementReader(EntityManagerFactory entityManagerFactory) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        return new JpaPagingItemReaderBuilder<VideoDailySettlementEntity>()
                .name("videoDailySettlementReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM video_daily_settlement v WHERE v.date = :today")
                .parameterValues(Map.of("today", today))
                .pageSize(100)  // 한번에 읽을 항목 수
                .build();
    }
}
