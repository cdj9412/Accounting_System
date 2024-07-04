package com.sparta.writer.monthly;

import com.sparta.entity.monthly.VideoMonthlySettlementEntity;
import com.sparta.repository.monthly.VideoMonthlySettlementRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoMonthlySettlementWriter")
public class VideoMonthlySettlementWriter implements ItemWriter<VideoMonthlySettlementEntity> {
    public VideoMonthlySettlementRepository videoMonthlySettlementRepository;

    @Override
    public void write(Chunk<? extends VideoMonthlySettlementEntity> chunk) throws Exception {
        // video_monthly_settlement 테이블에 데이터 저장
        for(VideoMonthlySettlementEntity entity : chunk) {
            Long videoId = entity.getVideoId();
            LocalDate startDate = entity.getMonthStartDate();
            LocalDate endDate = entity.getMonthEndDate();
            Optional<VideoMonthlySettlementEntity> existingData = videoMonthlySettlementRepository.findByDate(videoId, startDate, endDate);
            if(existingData.isPresent()) {
                log.info("월간 정산 데이터 존재함. 데이터 업데이트 entity: {}", entity);
                videoMonthlySettlementRepository.updateMonthlyStatistics(
                        entity.getVideoId(),
                        entity.getMonthStartDate(),
                        entity.getMonthEndDate(),
                        entity.getVideoSettlementAmount(),
                        entity.getAdSettlementAmount()
                );
            }
            else {
                log.info("신규 월간 정산 입력 entity: {}", entity);
                videoMonthlySettlementRepository.save(entity);
            }
        }
    }
}
