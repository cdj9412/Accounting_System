package com.sparta.writer;

import com.sparta.entity.VideoDailySettlementEntity;
import com.sparta.repository.VideoDailySettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoDailySettlementWriter")
public class VideoDailySettlementWriter {
    private final VideoDailySettlementRepository videoDailySettlementRepository;

    @Bean
    @Scope("step")
    public void videoDailySettlementWriterFirst(List<? extends VideoDailySettlementEntity> items) {
        if(items.isEmpty())  {
            log.info("일간 조회수 정산을 작성할 시청 내역이 존재하지 않습니다.");
            return;
        }

        for (VideoDailySettlementEntity entity : items) {
            Optional<VideoDailySettlementEntity> existingEntity = videoDailySettlementRepository.findByVideoIdAndDate(entity.getVideoId(), entity.getDate());
            if (existingEntity.isPresent()) {
                log.info("일간 정산 데이터 이미 존재함. 데이터 업데이트 entity: {}", entity);
                videoDailySettlementRepository.updateDailySettlement(
                        entity.getVideoId(),
                        entity.getDate(),
                        entity.getVideoSettlementAmount(),
                        entity.getAdSettlementAmount()
                );
            } else {
                log.info("신규 일간 정산 입력 entity: {}", entity);
                videoDailySettlementRepository.save(entity);
            }
        }
    }

    @Bean
    @Scope("step")
    public void videoDailySettlementWriterSecond(List<? extends VideoDailySettlementEntity> items) {
        if(items.isEmpty())  {
            log.info("일간 광고 조회수 정산을 작성할 시청 내역이 존재하지 않습니다.");
            return;
        }

        for (VideoDailySettlementEntity entity : items) {
            Optional<VideoDailySettlementEntity> existingEntity = videoDailySettlementRepository.findByVideoIdAndDate(entity.getVideoId(), entity.getDate());
            if (existingEntity.isPresent()) {
                log.info("광고 정산 금액 update entity: {}", entity);
                videoDailySettlementRepository.updateAdSettlementAmount(
                        entity.getVideoId(),
                        entity.getDate(),
                        entity.getAdSettlementAmount()
                );
            } else {
                log.warn("추가할 광고 정산 금액이 존재 하지 않음. entity: {}", entity);
            }
        }
    }
}
