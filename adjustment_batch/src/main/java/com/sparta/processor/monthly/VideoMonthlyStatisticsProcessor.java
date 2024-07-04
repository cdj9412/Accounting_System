package com.sparta.processor.monthly;

import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import com.sparta.entity.monthly.VideoMonthlyStatisticsEntity;
import com.sparta.repository.daily.VideoDailyStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoMonthlyStatisticsProcessor")
public class VideoMonthlyStatisticsProcessor implements ItemProcessor<VideoDailyStatisticsEntity, VideoMonthlyStatisticsEntity> {
    private VideoDailyStatisticsRepository videoDailyStatisticsRepository;

    @Override
    public VideoMonthlyStatisticsEntity process(VideoDailyStatisticsEntity item) throws Exception {
        VideoMonthlyStatisticsEntity monthlyStats = new VideoMonthlyStatisticsEntity();

        // 월간 통계 엔티티에 동영상 ID 설정
        Long videoId = item.getVideoId();
        monthlyStats.setVideoId(videoId);

        // 이번 달의 처음과 끝 구하기
        LocalDate today = item.getDate();
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        monthlyStats.setMonthStartDate(firstDayOfMonth);
        monthlyStats.setMonthEndDate(lastDayOfMonth);

        // 이번 달의 모든 조회수, 시청시간 합산
        Long monthViewCount = 0L;
        Long monthPlayTime = 0L;
        List<VideoDailyStatisticsEntity> monthData = videoDailyStatisticsRepository.findPeriodData(videoId,firstDayOfMonth,lastDayOfMonth);
        for (VideoDailyStatisticsEntity data : monthData) {
            monthViewCount += data.getViewCount();
            monthPlayTime += data.getPlayTime();
        }
        monthlyStats.setViewCount(monthViewCount);
        monthlyStats.setPlayTime(monthPlayTime);

        return monthlyStats;
    }
}
