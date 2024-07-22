package com.sparta.adjustment.reader;

import com.sparta.adjustment.entity.VideoDailyStatisticsEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementReader {
    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<VideoDailyStatisticsEntity> videoDailySettlementReader(
            @Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
            @Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
            @Value("#{jobParameters['date']}") LocalDate date,
            @Value("#{jobParameters['chunkSize']}") Integer chunkSize) throws Exception{

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("date", date);
        parameterValues.put("startVideoId", startVideoId);
        parameterValues.put("endVideoId", endVideoId);

        return new JdbcPagingItemReaderBuilder<VideoDailyStatisticsEntity>()
                .name("videoDailySettlementReader")
                .dataSource(dataSource)
                .queryProvider(createVideoStatisticsQueryProvider())
                .parameterValues(parameterValues)
                .pageSize(chunkSize)
                .rowMapper((rs, rowNum) -> new VideoDailyStatisticsEntity(
                        rs.getLong("video_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getLong("view_count"),
                        rs.getLong("ad_view_count"),
                        rs.getLong("play_time")
                ))
                .build();
    }

    private PagingQueryProvider createVideoStatisticsQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("SELECT /*+ INDEX(vs idx_daily_statistics_date_video_id) */ video_id, date, view_count, ad_view_count, play_time");
        factory.setFromClause("FROM video_daily_statistics vs");
        factory.setWhereClause("WHERE date = :date AND video_id BETWEEN :startVideoId AND :endVideoId");
        factory.setSortKey("video_id");

        return factory.getObject();
    }
}