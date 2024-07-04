package com.sparta.writer.weekly;

import com.sparta.entity.weekly.VideoWeeklySettlementEntity;
import com.sparta.repository.weekly.VideoWeeklySettlementRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoWeeklySettlementWriter")
public class VideoWeeklySettlementWriter implements ItemWriter<VideoWeeklySettlementEntity> {
    private VideoWeeklySettlementRepository videoWeeklySettlementRepository;

    @Override
    public void write(Chunk<? extends VideoWeeklySettlementEntity> chunk) throws Exception {
        // video_weekly_statistics 테이블에 데이터 저장
        for(VideoWeeklySettlementEntity entity : chunk) {
            Long videoId = entity.getVideoId();
            LocalDate monday = entity.getWeekStartDate();
            LocalDate sunday = entity.getWeekEndDate();
            Optional<VideoWeeklySettlementEntity> existingData = videoWeeklySettlementRepository.findByDate(videoId, monday, sunday);
            if(existingData.isPresent()) {
                log.info("주간 정산 데이터 존재함. 데이터 업데이트 entity: {}", entity);
                videoWeeklySettlementRepository.updateWeeklyStatistics(
                        entity.getVideoId(),
                        entity.getWeekStartDate(),
                        entity.getWeekEndDate(),
                        entity.getVideoSettlementAmount(),
                        entity.getAdSettlementAmount()
                );
            }
            else {
                log.info("신규 주간 정산 입력 entity: {}", entity);
                videoWeeklySettlementRepository.save(entity);
            }
        }

    }
}
