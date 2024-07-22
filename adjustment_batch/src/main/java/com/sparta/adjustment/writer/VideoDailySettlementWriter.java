package com.sparta.adjustment.writer;

import com.sparta.adjustment.entity.VideoDailySettlementEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoDailySettlementWriter")
public class VideoDailySettlementWriter implements ItemWriter<VideoDailySettlementEntity> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void write(Chunk<? extends VideoDailySettlementEntity> chunk) throws Exception {
        // 입력값이 0이면 기존 값 유지
        String sql = "INSERT INTO video_daily_settlement (video_id, date, video_settlement_amount, ad_settlement_amount) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "video_settlement_amount = VALUES(video_settlement_amount), " +
                "ad_settlement_amount = VALUES(ad_settlement_amount)";

        try{
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    VideoDailySettlementEntity entity = chunk.getItems().get(i);
                    ps.setLong(1, entity.getVideoId());
                    ps.setDate(2, java.sql.Date.valueOf(entity.getDate()));
                    ps.setLong(3, entity.getVideoSettlementAmount());
                    ps.setLong(4, entity.getAdSettlementAmount());
                }

                @Override
                public int getBatchSize() {
                    return chunk.getItems().size();
                }
            });

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving video_daily_settlement items", e);
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while saving video_daily_settlement items", e);
            throw e;
        }
    }

}
