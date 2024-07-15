package com.sparta.processor;

import com.sparta.entity.VideoAdDailyViewsEntity;
import com.sparta.entity.VideoDailySettlementEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "VideoAdDailySettlementProcessor")
public class VideoAdDailySettlementProcessor implements ItemProcessor<VideoAdDailyViewsEntity, VideoDailySettlementEntity> {
    private final UnitPriceCalculator unitPriceCalculator;

    @Override
    public VideoDailySettlementEntity process(VideoAdDailyViewsEntity item) throws Exception {
        // video_ad_daily_views 를 사용하여 video_daily_settlement 를 위한 데이터 처리
        try {
            VideoDailySettlementEntity adSettlement = new VideoDailySettlementEntity();
            adSettlement.setVideoId(item.getVideoId());
            adSettlement.setDate(item.getDate());

            Long datePrice = unitPriceCalculator.calculateDailyAdPrice(item.getVideoId(), item.getDate());
            adSettlement.setAdSettlementAmount(datePrice);

            log.info("{} - videoId : {}, date : {}, view : {} 계산 광고 단가 : {}",
                    Thread.currentThread().getName(), item.getVideoId(), item.getDate(), item.getViewCount(), datePrice);

            return adSettlement;
        } catch (Exception e) {
            log.error("Error processing item: {} {}", e, item);
            throw new RuntimeException(e);
        }
    }
}
