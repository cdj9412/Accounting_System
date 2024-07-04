package com.sparta.processor.monthly;

import com.sparta.entity.daily.VideoDailySettlementEntity;
import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import com.sparta.entity.monthly.VideoMonthlySettlementEntity;
import com.sparta.repository.daily.VideoDailySettlementRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j(topic = "VideoMonthlySettlementProcessor")
public class VideoMonthlySettlementProcessor implements ItemProcessor<VideoDailySettlementEntity, VideoMonthlySettlementEntity> {
    private VideoDailySettlementRepository videoDailySettlementRepository;

    @Override
    public VideoMonthlySettlementEntity process(VideoDailySettlementEntity item) throws Exception {
        VideoMonthlySettlementEntity monthlyStats = new VideoMonthlySettlementEntity();

        // 월간 정산 엔티티에 동영상 ID 설정
        Long videoId = item.getVideoId();
        monthlyStats.setVideoId(videoId);

        // 이번 달의 처음과 끝 구하기
        LocalDate today = item.getDate();
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        monthlyStats.setMonthStartDate(firstDayOfMonth);
        monthlyStats.setMonthEndDate(lastDayOfMonth);

        // 이번 달 까지의 조회수, 광고조회수 정산금액 합산
        Long monthViewsAmount = 0L;
        Long monthAdAmount = 0L;
        List<VideoDailySettlementEntity> monthData = videoDailySettlementRepository.findPeriodData(videoId,firstDayOfMonth,lastDayOfMonth);
        for(VideoDailySettlementEntity data : monthData) {
            monthViewsAmount += data.getVideoSettlementAmount();
            monthAdAmount += data.getAdSettlementAmount();
        }
        monthlyStats.setVideoSettlementAmount(monthViewsAmount);
        monthlyStats.setAdSettlementAmount(monthAdAmount);


        return monthlyStats;
    }
}
