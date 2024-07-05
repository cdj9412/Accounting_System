package com.sparta.service.impl;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.dto.response.TimeResponseDto;
import com.sparta.dto.response.TopTimeResponseDto;
import com.sparta.dto.response.TopViewResponseDto;
import com.sparta.dto.response.ViewResponseDto;
import com.sparta.entity.statistics.VideoDailyStatisticsEntity;
import com.sparta.repository.statistics.VideoDailyStatisticsRepository;
import com.sparta.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final VideoDailyStatisticsRepository videoDailyStatisticsRepository;

    @Override
    public ViewResponseDto findDailyView(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 조회수 top 5
        List<VideoDailyStatisticsEntity> viewEntityList = videoDailyStatisticsRepository.findViewTop5(userId, today);
        List<TopViewResponseDto> topViewResponseDtoList = new ArrayList<>();
        for (VideoDailyStatisticsEntity viewEntity : viewEntityList) {
            topViewResponseDtoList.add(new TopViewResponseDto(viewEntity.getVideoId(), viewEntity.getViewCount()));
        }

        if (topViewResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }
        return ViewResponseDto.from(responseCode, responseMessage, topViewResponseDtoList);
    }

    @Override
    public ViewResponseDto findWeeklyView(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return makeViewResponse(userId, monday, sunday);
    }

    @Override
    public ViewResponseDto findMonthlyView(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        return makeViewResponse(userId, firstDayOfMonth, lastDayOfMonth);
    }

    private ViewResponseDto makeViewResponse(String userId, LocalDate startDate, LocalDate endDate) {
        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;
        // 최고 조회수 top 5
        List<Object[]> viewObjectList = videoDailyStatisticsRepository.findPeriodViewTop5(userId, startDate, endDate);
        List<TopViewResponseDto> topViewResponseDtoList = new ArrayList<>();
        for (Object[] viewObject : viewObjectList) {
            topViewResponseDtoList.add(new TopViewResponseDto((Long)viewObject[0], (Long)viewObject[1]));
        }
        if (topViewResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return ViewResponseDto.from(responseCode, responseMessage, topViewResponseDtoList);
    }

    @Override
    public TimeResponseDto findDailyTime(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 재생시간 top 5
        List<VideoDailyStatisticsEntity> timeEntityList = videoDailyStatisticsRepository.findTimeTop5(userId, today);
        List<TopTimeResponseDto> topTimeResponseDtoList = new ArrayList<>();
        for (VideoDailyStatisticsEntity timeEntity : timeEntityList) {
            topTimeResponseDtoList.add(new TopTimeResponseDto(timeEntity.getVideoId(), timeEntity.getPlayTime()));
        }

        if (topTimeResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return TimeResponseDto.from(responseCode, responseMessage, topTimeResponseDtoList);
    }

    @Override
    public TimeResponseDto findWeeklyTime(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return makeTimeResponse(userId, monday, sunday);
    }

    @Override
    public TimeResponseDto findMonthlyTime(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        return makeTimeResponse(userId, firstDayOfMonth, lastDayOfMonth);
    }

    private TimeResponseDto makeTimeResponse(String userId, LocalDate startDate, LocalDate endDate) {
        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;
        // 최고 조회수 top 5
        List<Object[]> timeObjectList = videoDailyStatisticsRepository.findPeriodTimeTop5(userId, startDate, endDate);
        List<TopTimeResponseDto> topTimeResponseDtoList = new ArrayList<>();
        for (Object[] timeObject : timeObjectList) {
            topTimeResponseDtoList.add(new TopTimeResponseDto((Long)timeObject[0], (Long)timeObject[1]));
        }
        if (topTimeResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return TimeResponseDto.from(responseCode, responseMessage, topTimeResponseDtoList);
    }
}
