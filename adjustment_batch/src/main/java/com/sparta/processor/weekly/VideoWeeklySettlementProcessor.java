package com.sparta.processor.weekly;

import com.sparta.entity.daily.VideoDailySettlementEntity;
import com.sparta.entity.weekly.VideoWeeklySettlementEntity;
import com.sparta.repository.daily.VideoDailySettlementRepository;
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
@Slf4j(topic = "VideoWeeklySettlementProcessor")
public class VideoWeeklySettlementProcessor implements ItemProcessor<VideoDailySettlementEntity, VideoWeeklySettlementEntity> {
    private VideoDailySettlementRepository videoDailySettlementRepository;

    @Override
    public VideoWeeklySettlementEntity process(VideoDailySettlementEntity item) throws Exception {
        VideoWeeklySettlementEntity weeklySettle = new VideoWeeklySettlementEntity();

        // 주간 정산 엔티티에 동영상 ID 설정
        Long videoId = item.getVideoId();
        weeklySettle.setVideoId(videoId);

        // 이번 주의 월요일과 일요일 구하기
        LocalDate today = item.getDate();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        weeklySettle.setWeekStartDate(monday);
        weeklySettle.setWeekEndDate(sunday);

        // 월요일 부터 일요일 까지의 조회수, 광고조회수 정산금액 합산
        Long weekViewsAmount = 0L;
        Long weekAdAmount = 0L;
        List<VideoDailySettlementEntity> weekData = videoDailySettlementRepository.findPeriodData(videoId,monday,sunday);
        for(VideoDailySettlementEntity data : weekData){
            weekViewsAmount += data.getVideoSettlementAmount();
            weekAdAmount += data.getAdSettlementAmount();
        }
        weeklySettle.setVideoSettlementAmount(weekViewsAmount);
        weeklySettle.setAdSettlementAmount(weekAdAmount);


        return weeklySettle;
    }
}
