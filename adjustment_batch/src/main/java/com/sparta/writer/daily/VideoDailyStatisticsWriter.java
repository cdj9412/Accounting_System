package com.sparta.writer.daily;

import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import com.sparta.repository.daily.VideoDailyStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoDailyStatisticsWriter")
public class VideoDailyStatisticsWriter implements ItemWriter<VideoDailyStatisticsEntity> {
    private VideoDailyStatisticsRepository videoDailyStatisticsRepository;

    @Override
    public void write(Chunk<? extends VideoDailyStatisticsEntity> chunk) throws Exception {
        // video_daily_statistics 테이블에 데이터 저장
        for(VideoDailyStatisticsEntity entity : chunk) {
            Optional<VideoDailyStatisticsEntity> existingData = videoDailyStatisticsRepository.findByDate(entity.getVideoId());
            if(existingData.isPresent()) {
                log.info("일간 통계 데이터 존재함. 데이터 업데이트 entity: {}", entity);
                videoDailyStatisticsRepository.updateDailyStatistics(
                        entity.getVideoId(),
                        entity.getDate(),
                        entity.getViewCount(),
                        entity.getPlayTime()
                );
            }
            else {
                log.info("신규 일간 통계 입력 entity: {}", entity);
                videoDailyStatisticsRepository.save(entity);
            }
        }
    }
}
