//package com.sparta.adjustment.processor;
//
//import com.sparta.adjustment.entity.VideoDailyStatisticsEntity;
//import com.sparta.adjustment.entity.VideoDailyViewsEntity;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j(topic = "VideoDailyStatisticsProcessor")
//public class VideoDailyStatisticsProcessor implements ItemProcessor<VideoDailyViewsEntity, VideoDailyStatisticsEntity> {
//
//    @Override
//    public VideoDailyStatisticsEntity process(VideoDailyViewsEntity item) throws Exception {
//
//        // video_daily_statistics 를 위한 데이터 처리
//        VideoDailyStatisticsEntity dailyStatistics = new VideoDailyStatisticsEntity();
//        dailyStatistics.setVideoId(item.getVideoId());
//        dailyStatistics.setDate(item.getDate());
//        dailyStatistics.setViewCount(item.getViewCount());
//        dailyStatistics.setPlayTime(item.getWatchTime());
//
//        return dailyStatistics;
//    }
//}
