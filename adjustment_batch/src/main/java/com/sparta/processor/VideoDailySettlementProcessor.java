package com.sparta.processor;

import com.sparta.entity.VideoDailySettlementEntity;
import com.sparta.entity.VideoDailyViewsEntity;
import lombok.AllArgsConstructor;
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
        VideoDailySettlementEntity viewsSettlement = new VideoDailySettlementEntity();
        viewsSettlement.setVideoId(item.getVideoId());
        viewsSettlement.setDate(item.getDate());
        // 여기서 조회수 별 단가 조정 필요
        log.error("videoId : {}, date : {}", item.getVideoId(), item.getDate());
        Long datePrice = unitPriceCalculator.calculateDailyViewPrice(item.getVideoId(), item.getDate());
        viewsSettlement.setVideoSettlementAmount(datePrice);

        log.error("videoId : {}, date : {}, view : {}", item.getVideoId(), item.getDate(), item.getViewCount());
        log.error("계산 광고 단가 : {}", datePrice);

        return viewsSettlement;
    }
}