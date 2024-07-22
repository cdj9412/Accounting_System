package com.sparta.adjustment.reader;

import com.sparta.adjustment.entity.VideoDailyViewsEntity;
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
@Slf4j(topic = "VideoDailyViewsReader")
public class VideoDailyViewsReader {
    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<VideoDailyViewsEntity> videoDailyViewsEntityReader(
            @Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
            @Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
            @Value("#{jobParameters['date']}") LocalDate date,
            @Value("#{jobParameters['chunkSize']}") Integer chunkSize) throws Exception{

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("date", date);
        parameterValues.put("startVideoId", startVideoId);
        parameterValues.put("endVideoId", endVideoId);

        return new JdbcPagingItemReaderBuilder<VideoDailyViewsEntity>()
                .name("videoDailyViewsEntityReader")
                .dataSource(dataSource)
                .queryProvider(createVideoDailyViewsQueryProvider())
                .parameterValues(parameterValues)
                .pageSize(chunkSize)
                .rowMapper((rs, rowNum) -> new VideoDailyViewsEntity(
                        rs.getLong("video_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getLong("view_count"),
                        rs.getLong("watch_time")
                ))
                .build();
    }

    private PagingQueryProvider createVideoDailyViewsQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("SELECT /*+ INDEX(views idx_video_daily_views_date_video_id) */ video_id, date, view_count, watch_time");
        factory.setFromClause("FROM video_daily_views views");
        factory.setWhereClause("WHERE date = :date AND video_id BETWEEN :startVideoId AND :endVideoId");
        factory.setSortKey("video_id");

        return factory.getObject();
    }
}
