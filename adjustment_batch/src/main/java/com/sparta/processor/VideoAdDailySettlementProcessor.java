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
        VideoDailySettlementEntity adSettlement = new VideoDailySettlementEntity();
        adSettlement.setVideoId(item.getVideoId());
        adSettlement.setDate(item.getDate());
        // 여기도 조회 수 별 광고 단가 조정 필요
        Long datePrice = unitPriceCalculator.calculateDailyAdPrice(item.getVideoId(), item.getDate());
        adSettlement.setAdSettlementAmount(datePrice);

        log.error("videoId : {}, date : {}, view : {}", item.getVideoId(), item.getDate(), item.getViewCount());
        log.error("계산 광고 단가 : {}", datePrice);

        return adSettlement;
    }
}
