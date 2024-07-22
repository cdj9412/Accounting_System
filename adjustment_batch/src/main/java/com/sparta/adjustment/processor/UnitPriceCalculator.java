//package com.sparta.adjustment.processor;
//
//import com.sparta.adjustment.repository.VideoRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//@Slf4j(topic = "UnitPriceCalculator")
//public class UnitPriceCalculator {
//    private final VideoRepository videoRepository;
//
//    /**
//     * 주어진 비디오 ID와 특정 날짜에 대해 일일 조회수 단가를 계산합니다.
//     *
//     * @param videoId 비디오 ID
//     * @param dailyViews 조회 수
//     * @return 주어진 날짜에 대한 일일 조회수 단가
//     */
//    public Long calculateDailyViewPrice(Long videoId, Long dailyViews) {
//        Long totalViews = videoRepository.findTotalViewsById(videoId);
//        Long previousTotalViews = totalViews - dailyViews;
//        return calculateViewPrice(previousTotalViews, dailyViews);
//    }
//
//    /**
//     * 주어진 비디오 ID와 특정 날짜에 대해 일일 광고 조회수 단가를 계산합니다.
//     *
//     * @param videoId 비디오 ID
//     * @param dailyViews 조회 수
//     * @param dailyAdViews 광고 조회 수
//     * @return 주어진 날짜에 대한 일일 광고 조회수 단가
//     */
//    public Long calculateDailyAdPrice(Long videoId, Long dailyViews, Long dailyAdViews) {
//        Long totalViews = videoRepository.findTotalViewsById(videoId);
//        Long previousTotalViews = totalViews - dailyViews;
//        return calculateAdPrice(previousTotalViews, dailyAdViews);
//    }
//    /**
//     * 주어진 일일 조회수에 따른 조회수 단가를 계산합니다.
//     *
//     * @param previousTotalViews 이전 날짜까지의 누적 조회수
//     * @param dailyViews         일일 조회수
//     * @return 주어진 일일 조회수에 따른 계산된 조회수 단가
//     */
//    private Long calculateViewPrice(Long previousTotalViews, Long dailyViews) {
//        long price = 0;
//        boolean calculated = false;
//
//        // 100만 이상
//        if (previousTotalViews > 1000000) {
//            price += (long) (dailyViews * 1.5);
//            calculated = true;
//        }
//
//        // 50만 이상 100만 미만
//        if (previousTotalViews > 500000 && !calculated) {
//            price += (long) (dailyViews * 1.3);
//            calculated = true;
//        }
//
//        // 10만 이상 50만 미만
//        if (previousTotalViews > 100000 && !calculated) {
//            price += (long) (dailyViews * 1.1);
//            calculated = true;
//        }
//
//        // 10만 미만
//        if (!calculated) {
//            price += dailyViews;
//        }
//
//        // 1의 자리 절사
//        return (price / 10) * 10;
//    }
//
//    /**
//     * 주어진 일일 조회수에 따른 광고 단가를 계산합니다.
//     *
//     * @param previousTotalViews 이전 날짜까지의 누적 조회수
//     * @param dailyAdViews         일일 조회수
//     * @return 주어진 일일 조회수에 따른 광고 조회수 계산된 단가
//     */
//    private Long calculateAdPrice(Long previousTotalViews, Long dailyAdViews) {
//        long price = 0;
//        boolean calculated = false;
//
//        // 100만 이상
//        if (previousTotalViews > 1000000) {
//            price += dailyAdViews * 20;
//            calculated = true;
//        }
//
//        // 50만 이상 100만 미만
//        if (previousTotalViews > 500000 && !calculated) {
//            price += dailyAdViews * 15;
//            calculated = true;
//        }
//
//        // 10만 이상 50만 미만
//        if (previousTotalViews > 100000 && !calculated) {
//            price += dailyAdViews * 12;
//            calculated = true;
//        }
//
//        // 10만 미만
//        if (!calculated) {
//            price += dailyAdViews * 10;
//        }
//
//        // 1의 자리 절사
//        return (price / 10) * 10;
//    }
//}
