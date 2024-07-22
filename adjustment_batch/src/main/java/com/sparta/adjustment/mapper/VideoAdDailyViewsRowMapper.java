package com.sparta.adjustment.mapper;

import com.sparta.adjustment.entity.VideoAdDailyViewsEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoAdDailyViewsRowMapper implements RowMapper<VideoAdDailyViewsEntity> {
    @Override
    public VideoAdDailyViewsEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return VideoAdDailyViewsEntity.builder()
                .videoId(rs.getLong("video_id"))
                .adId(rs.getLong("ad_id"))
                .date(rs.getDate("date").toLocalDate())
                .viewCount(rs.getLong("view_count"))
                .build();
    }
}
