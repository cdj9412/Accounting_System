package com.sparta.adjustment.reader;

import com.sparta.adjustment.entity.VideoAdDailyViewsEntity;
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
@Slf4j(topic = "VideoAdDailyViewsReader")
public class VideoAdDailyViewsReader {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<VideoAdDailyViewsEntity> videoAdDailyViewsEntityReader(
            @Value("#{stepExecutionContext['startVideoId']}") Long startVideoId,
            @Value("#{stepExecutionContext['endVideoId']}") Long endVideoId,
            @Value("#{jobParameters['date']}") LocalDate date,
            @Value("#{jobParameters['chunkSize']}") Integer chunkSize) throws Exception {

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("date", date);
        parameterValues.put("startVideoId", startVideoId);
        parameterValues.put("endVideoId", endVideoId);

        return new JdbcPagingItemReaderBuilder<VideoAdDailyViewsEntity>()
                .name("videoAdDailyViewsEntityReader")
                .dataSource(dataSource)
                .queryProvider(createVideoAdDailyViewsQueryProvider())
                .parameterValues(parameterValues)
                .pageSize(chunkSize)
                .rowMapper((rs, rowNum) -> new VideoAdDailyViewsEntity(
                        rs.getLong("video_id"),
                        rs.getLong("ad_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getLong("view_count")
                ))
                .build();
    }

    private PagingQueryProvider createVideoAdDailyViewsQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("SELECT /*+ INDEX(views idx_ad_video_daily_views_date_video_id) */ video_id, ad_id, date, view_count");
        factory.setFromClause("FROM video_ad_daily_views views");
        factory.setWhereClause("WHERE date = :date AND video_id BETWEEN :startVideoId AND :endVideoId");
        factory.setSortKey("video_id");

        return factory.getObject();
    }
}
