package com.sparta.writer.monthly;

import com.sparta.entity.monthly.VideoMonthlyStatisticsEntity;
import com.sparta.repository.monthly.VideoMonthlyStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoMonthlyStatisticsWriter")
public class VideoMonthlyStatisticsWriter implements ItemWriter<VideoMonthlyStatisticsEntity> {
    public VideoMonthlyStatisticsRepository videoMonthlyStatisticsRepository;

    @Override
    public void write(Chunk<? extends VideoMonthlyStatisticsEntity> chunk) throws Exception {
        // video_monthly_statistics 테이블에 데이터 저장
        for(VideoMonthlyStatisticsEntity entity : chunk) {
            Long videoId = entity.getVideoId();
            LocalDate startDate = entity.getMonthStartDate();
            LocalDate endDate = entity.getMonthEndDate();
            Optional<VideoMonthlyStatisticsEntity> existingData = videoMonthlyStatisticsRepository.findByDate(videoId, startDate, endDate);
            if(existingData.isPresent()) {
                log.info("월간 통계 데이터 존재함. 데이터 업데이트 entity: {}", entity);
                videoMonthlyStatisticsRepository.updateMonthlyStatistics(
                        entity.getVideoId(),
                        entity.getMonthStartDate(),
                        entity.getMonthEndDate(),
                        entity.getViewCount(),
                        entity.getPlayTime()
                );
            }
            else {
                log.info("신규 월간 통계 입력 entity: {}", entity);
                videoMonthlyStatisticsRepository.save(entity);
            }
        }
    }
}
