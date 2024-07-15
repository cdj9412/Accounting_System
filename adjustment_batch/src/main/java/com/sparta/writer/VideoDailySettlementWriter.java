package com.sparta.writer;

import com.sparta.entity.VideoDailySettlementEntity;
import com.sparta.repository.VideoDailySettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoDailySettlementWriter")
public class VideoDailySettlementWriter {
    private final VideoDailySettlementRepository videoDailySettlementRepository;

    @Bean
    @StepScope
    public ItemWriter<VideoDailySettlementEntity> videoDailySettlementWriterFirst() {
        return new ItemWriter<VideoDailySettlementEntity>() {
            @Override
            public void write(Chunk<? extends VideoDailySettlementEntity> chunk) throws Exception {
                if(chunk.isEmpty())  {
                    log.info("일간 조회수 정산을 작성할 시청 내역이 존재하지 않습니다.");
                    return;
                }

                for (VideoDailySettlementEntity entity : chunk) {
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
        };
    }

    @Bean
    @StepScope
    public ItemWriter<VideoDailySettlementEntity> videoDailySettlementWriterSecond() {
        return new ItemWriter<VideoDailySettlementEntity>() {
            @Override
            public void write(Chunk<? extends VideoDailySettlementEntity> chunk) throws Exception {
                if(chunk.isEmpty())  {
                    log.info("일간 광고 조회수 정산을 작성할 시청 내역이 존재하지 않습니다.");
                    return;
                }

                for (VideoDailySettlementEntity entity : chunk) {
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
        };
    }

    /*@Bean
    @StepScope
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
    @StepScope
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
    }*/
}
