package com.sparta.processor.weekly;

import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import com.sparta.entity.weekly.VideoWeeklyStatisticsEntity;
import com.sparta.repository.daily.VideoDailyStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoWeeklyStatisticsProcessor")
public class VideoWeeklyStatisticsProcessor implements ItemProcessor<VideoDailyStatisticsEntity, VideoWeeklyStatisticsEntity> {
    private VideoDailyStatisticsRepository videoDailyStatisticsRepository;

    @Override
    public VideoWeeklyStatisticsEntity process(VideoDailyStatisticsEntity item) throws Exception {
        VideoWeeklyStatisticsEntity weeklyStats = new VideoWeeklyStatisticsEntity();

        // 주간 통계 엔티티에 동영상 ID 설정
        Long videoId = item.getVideoId();
        weeklyStats.setVideoId(videoId);

        // 이번 주의 월요일과 일요일 구하기
        LocalDate today = item.getDate();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        weeklyStats.setWeekStartDate(monday);
        weeklyStats.setWeekEndDate(sunday);

        // 월요일 부터 일요일 까지의 조회수, 시청시간 합산
        Long weekViewCount = 0L;
        Long weekPlayTime = 0L;
        List<VideoDailyStatisticsEntity> weekData = videoDailyStatisticsRepository.findPeriodData(videoId,monday,sunday);
        for(VideoDailyStatisticsEntity data : weekData){
            weekViewCount += data.getViewCount();
            weekPlayTime += data.getPlayTime();
        }
        weeklyStats.setViewCount(weekViewCount);
        weeklyStats.setPlayTime(weekPlayTime);

        return weeklyStats;
    }
}
