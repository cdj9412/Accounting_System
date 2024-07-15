package com.sparta.processor;

import com.sparta.entity.VideoDailySettlementEntity;
import com.sparta.entity.VideoDailyViewsEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoDailyStatisticsProcessor")
public class VideoDailySettlementProcessor implements ItemProcessor<VideoDailyViewsEntity, VideoDailySettlementEntity> {
    private final UnitPriceCalculator unitPriceCalculator;

    @Override
    public VideoDailySettlementEntity process(VideoDailyViewsEntity item) throws Exception {
        // video_daily_views 를 사용하여 video_daily_settlement 를 위한 데이터 처리
        try {
            VideoDailySettlementEntity viewsSettlement = new VideoDailySettlementEntity();
            viewsSettlement.setVideoId(item.getVideoId());
            viewsSettlement.setDate(item.getDate());

            Long datePrice = unitPriceCalculator.calculateDailyViewPrice(item.getVideoId(), item.getDate());
            viewsSettlement.setVideoSettlementAmount(datePrice);

            log.info("{} - videoId : {}, date : {}, view : {} 계산 조회수 단가 : {}",
                    Thread.currentThread().getName(), item.getVideoId(), item.getDate(), item.getViewCount(), datePrice);

            return viewsSettlement;
        }catch (Exception e) {
            log.error("Error processing item: {} {}", e, item);
            throw new RuntimeException(e);
        }
    }
}