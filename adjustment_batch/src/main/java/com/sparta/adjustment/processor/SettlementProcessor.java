package com.sparta.adjustment.processor;


import com.sparta.adjustment.entity.VideoDailySettlementEntity;
import com.sparta.adjustment.entity.VideoDailyStatisticsEntity;
import com.sparta.adjustment.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementProcessor implements ItemProcessor<VideoDailyStatisticsEntity, VideoDailySettlementEntity> {
    private final VideoRepository videoRepository;

    @Override
    public VideoDailySettlementEntity process(VideoDailyStatisticsEntity item) throws Exception {
        try {
            Long videoId = item.getVideoId();
            Long totalView = videoRepository.findTotalViewsById(videoId);

            Long viewCount = item.getViewCount();
            Long adViewCount = item.getAdViewCount();

            Long viewPrice = calculateViewCost(totalView, viewCount);
            Long adPrice = calculateAdCost(totalView, viewCount, adViewCount);

            return VideoDailySettlementEntity.builder()
                    .videoId(videoId)
                    .date(item.getDate())
                    .videoSettlementAmount(viewPrice)
                    .adSettlementAmount(adPrice)
                    .build();
        }catch (Exception e) {
            log.error("Error processing item: {} {}", e, item);
            throw new RuntimeException(e);
        }
    }

    private long calculateViewCost(long totalViews, long views) {
        long currentTotalViews = totalViews - views;
        double[] rates = {1.0, 1.1, 1.3, 1.5};
        return calculateCost(currentTotalViews, views, rates);
    }

    private long calculateAdCost(long totalViews, long views, long adViews) {
        long currentTotalViews = totalViews - views;
        double[] rates = {10.0, 12.0, 15.0, 20.0};
        return calculateCost(currentTotalViews, adViews, rates);
    }

    private static long calculateCost(long currentTotalViews, long remainingViews, double[] rates) {
        double cost = 0.0;
        int[] thresholds = {100000, 500000, 1000000};

        for (int i = 0; i < thresholds.length; i++) {
            if (currentTotalViews < thresholds[i]) {
                long viewsInThisTier = Math.min(remainingViews, thresholds[i] - currentTotalViews);
                cost += (viewsInThisTier * rates[i]);
                remainingViews -= viewsInThisTier;
                currentTotalViews += viewsInThisTier;
            }

            if (remainingViews <= 0) {
                break;
            }
        }

        if (remainingViews > 0) {
            cost += (remainingViews * rates[rates.length - 1]);
        }

        return (long)cost;
    }
}
