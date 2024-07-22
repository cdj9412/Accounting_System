package com.sparta.adjustment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VideoRepository {
    private final JdbcTemplate jdbcTemplate;

    public Long findTotalViewsById(Long id) {
        String sql = "SELECT total_views FROM video WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    };
}
