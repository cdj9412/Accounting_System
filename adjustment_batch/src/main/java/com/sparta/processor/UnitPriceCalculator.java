package com.sparta.processor;

import com.sparta.entity.VideoAdDailyViewsEntity;
import com.sparta.entity.VideoEntity;
import com.sparta.repository.VideoAdDailyViewsRepository;
import com.sparta.repository.VideoDailyViewsRepository;
import com.sparta.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j(topic = "UnitPriceCalculator")
public class UnitPriceCalculator {
    private final VideoRepository videoRepository;
    private final VideoDailyViewsRepository videoDailyViewsRepository;
    private final VideoAdDailyViewsRepository videoAdDailyViewsRepository;

    /**
     * 주어진 비디오 ID와 특정 날짜에 대해 일일 조회수 단가를 계산합니다.
     *
     * @param videoId 비디오 ID
     * @param date 특정 날짜
     * @return 주어진 날짜에 대한 일일 조회수 단가
     */
    public Long calculateDailyViewPrice(Long videoId, LocalDate date) {
        Optional<Long> dailyViews = videoDailyViewsRepository.findViewCountByVideoIdAndDate(videoId, date);
        Long previousTotalViews = getTotalViews(videoId,date);
        return calculateViewPrice(previousTotalViews, dailyViews.get());
    }

    /**
     * 주어진 비디오 ID와 특정 날짜에 대해 일일 광고 조회수 단가를 계산합니다.
     *
     * @param videoId 비디오 ID
     * @param date 특정 날짜
     * @return 주어진 날짜에 대한 일일 광고 조회수 단가
     */
    public Long calculateDailyAdPrice(Long videoId, LocalDate date) {
        List<VideoAdDailyViewsEntity> adDailyViewsEntities = videoAdDailyViewsRepository.findByVideoIdAndDate(videoId, date);
        Long dailyAdViews = adDailyViewsEntities.stream().mapToLong(VideoAdDailyViewsEntity::getViewCount).sum();
        Long previousTotalViews = getTotalViews(videoId, date);
        return calculateAdPrice(previousTotalViews, dailyAdViews);
    }

    /**
     * 전체 누적 조회수를 가져옵니다.
     *
     * @param videoId 비디오 ID
     * @param date 특정 날짜
     * @return 해당 영상의 총 누적 조회수에서 오늘의 조회 수를 뺀 값
     */
    private Long getTotalViews(Long videoId, LocalDate date) {
        Optional<Long> todayViewCount = videoDailyViewsRepository.findViewCountByVideoIdAndDate(videoId, date);
        Optional<VideoEntity> entity = videoRepository.findById(videoId);
        return entity.map(videoEntity -> videoEntity.getTotalViews() - todayViewCount.get()).orElse(0L);
    }

    /**
     * 주어진 일일 조회수에 따른 조회수 단가를 계산합니다.
     *
     * @param previousTotalViews 이전 날짜까지의 누적 조회수
     * @param dailyViews         일일 조회수
     * @return 주어진 일일 조회수에 따른 계산된 조회수 단가
     */
    private Long calculateViewPrice(Long previousTotalViews, Long dailyViews) {
        long price = 0;
        boolean calculated = false;

        // 100만 이상
        if (previousTotalViews > 1000000) {
            price += (long) (dailyViews * 1.5);
            calculated = true;
        }

        // 50만 이상 100만 미만
        if (previousTotalViews > 500000 && !calculated) {
            price += (long) (dailyViews * 1.3);
            calculated = true;
        }

        // 10만 이상 50만 미만
        if (previousTotalViews > 100000 && !calculated) {
            price += (long) (dailyViews * 1.1);
            calculated = true;
        }

        // 10만 미만
        if (!calculated) {
            price += dailyViews;
        }

        // 1의 자리 절사
        return (price / 10) * 10;
    }

    /**
     * 주어진 일일 조회수에 따른 광고 단가를 계산합니다.
     *
     * @param previousTotalViews 이전 날짜까지의 누적 조회수
     * @param dailyAdViews         일일 조회수
     * @return 주어진 일일 조회수에 따른 광고 조회수 계산된 단가
     */
    private Long calculateAdPrice(Long previousTotalViews, Long dailyAdViews) {
        long price = 0;
        boolean calculated = false;

        // 100만 이상
        if (previousTotalViews > 1000000) {
            price += dailyAdViews * 20;
            calculated = true;
        }

        // 50만 이상 100만 미만
        if (previousTotalViews > 500000 && !calculated) {
            price += dailyAdViews * 15;
            calculated = true;
        }

        // 10만 이상 50만 미만
        if (previousTotalViews > 100000 && !calculated) {
            price += dailyAdViews * 12;
            calculated = true;
        }

        // 10만 미만
        if (!calculated) {
            price += dailyAdViews * 10;
        }

        // 1의 자리 절사
        return (price / 10) * 10;
    }
}
