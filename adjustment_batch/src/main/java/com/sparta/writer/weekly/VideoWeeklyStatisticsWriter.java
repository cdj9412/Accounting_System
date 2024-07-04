package com.sparta.writer.weekly;

import com.sparta.entity.weekly.VideoWeeklyStatisticsEntity;
import com.sparta.repository.weekly.VideoWeeklyStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoWeeklyStatisticsWriter")
public class VideoWeeklyStatisticsWriter implements ItemWriter<VideoWeeklyStatisticsEntity> {
    private VideoWeeklyStatisticsRepository videoWeeklyStatisticsRepository;

    @Override
    public void write(Chunk<? extends VideoWeeklyStatisticsEntity> chunk) throws Exception {
        // video_weekly_statistics 테이블에 데이터 저장
        for(VideoWeeklyStatisticsEntity entity : chunk) {
            Long videoId = entity.getVideoId();
            LocalDate monday = entity.getWeekStartDate();
            LocalDate sunday = entity.getWeekEndDate();
            Optional<VideoWeeklyStatisticsEntity> existingData = videoWeeklyStatisticsRepository.findByDate(videoId, monday, sunday);
            if(existingData.isPresent()) {
                log.info("주간 통계 데이터 존재함. 데이터 업데이트 entity: {}", entity);
                videoWeeklyStatisticsRepository.updateWeeklyStatistics(
                        entity.getVideoId(),
                        entity.getWeekStartDate(),
                        entity.getWeekEndDate(),
                        entity.getViewCount(),
                        entity.getPlayTime()
                );
            }
            else {
                log.info("신규 주간 통계 입력 entity: {}", entity);
                videoWeeklyStatisticsRepository.save(entity);
            }
        }

    }
}
