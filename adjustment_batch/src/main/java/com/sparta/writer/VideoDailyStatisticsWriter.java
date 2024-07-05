package com.sparta.writer;

import com.sparta.entity.VideoDailyStatisticsEntity;
import com.sparta.repository.VideoDailyStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoDailyStatisticsWriter")
public class VideoDailyStatisticsWriter implements ItemWriter<VideoDailyStatisticsEntity> {
    private final VideoDailyStatisticsRepository videoDailyStatisticsRepository;

    @Override
    public void write(Chunk<? extends VideoDailyStatisticsEntity> chunk) throws Exception {
        if(chunk.isEmpty())  {
            log.info("일일 통계를 작성할 시청 내역이 존재하지 않습니다.");
            return;
        }

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
