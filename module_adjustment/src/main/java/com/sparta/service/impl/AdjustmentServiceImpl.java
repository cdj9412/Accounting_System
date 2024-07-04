package com.sparta.service.impl;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.dto.response.TimeResponseDto;
import com.sparta.dto.response.ViewResponseDto;
import com.sparta.dto.response.TopTimeResponseDto;
import com.sparta.dto.response.TopViewResponseDto;
import com.sparta.entity.statistics.VideoDailyStatisticsEntity;
import com.sparta.entity.statistics.VideoMonthlyStatisticsEntity;
import com.sparta.entity.statistics.VideoWeeklyStatisticsEntity;
import com.sparta.repository.statistics.VideoDailyStatisticsRepository;
import com.sparta.repository.statistics.VideoMonthlyStatisticsRepository;
import com.sparta.repository.statistics.VideoWeeklyStatisticsRepository;
import com.sparta.service.AdjustmentService;
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
public class AdjustmentServiceImpl implements AdjustmentService {
    private VideoDailyStatisticsRepository videoDailyStatisticsRepository;
    private VideoWeeklyStatisticsRepository videoWeeklyStatisticsRepository;
    private VideoMonthlyStatisticsRepository videoMonthlyStatisticsRepository;

    @Override
    public ViewResponseDto findDailyView() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 조회수 top 5
        List<VideoDailyStatisticsEntity> viewEntityList = videoDailyStatisticsRepository.findViewTop5(today);
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
    public ViewResponseDto findWeeklyView() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 조회수 top 5
        List<VideoWeeklyStatisticsEntity> viewEntityList = videoWeeklyStatisticsRepository.findViewTop5(monday, sunday);
        List<TopViewResponseDto> topViewResponseDtoList = new ArrayList<>();
        for (VideoWeeklyStatisticsEntity viewEntity : viewEntityList) {
            topViewResponseDtoList.add(new TopViewResponseDto(viewEntity.getVideoId(), viewEntity.getViewCount()));
        }
        if (topViewResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return ViewResponseDto.from(responseCode, responseMessage, topViewResponseDtoList);
    }

    @Override
    public ViewResponseDto findMonthlyView() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 조회수 top 5
        List<VideoMonthlyStatisticsEntity> viewEntityList = videoMonthlyStatisticsRepository.findViewTop5(firstDayOfMonth, lastDayOfMonth);
        List<TopViewResponseDto> topViewResponseDtoList = new ArrayList<>();
        for (VideoMonthlyStatisticsEntity viewEntity : viewEntityList) {
            topViewResponseDtoList.add(new TopViewResponseDto(viewEntity.getVideoId(), viewEntity.getViewCount()));
        }
        if (topViewResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return ViewResponseDto.from(responseCode, responseMessage, topViewResponseDtoList);
    }

    @Override
    public TimeResponseDto findDailyTime() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 재생시간 top 5
        List<VideoDailyStatisticsEntity> timeEntityList = videoDailyStatisticsRepository.findTimeTop5(today);
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
    public TimeResponseDto findWeeklyTime() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 재생시간 top 5
        List<VideoWeeklyStatisticsEntity> timeEntityList = videoWeeklyStatisticsRepository.findTimeTop5(monday, sunday);
        List<TopTimeResponseDto> topTimeResponseDtoList = new ArrayList<>();
        for (VideoWeeklyStatisticsEntity timeEntity : timeEntityList) {
            topTimeResponseDtoList.add(new TopTimeResponseDto(timeEntity.getVideoId(), timeEntity.getPlayTime()));
        }

        if (topTimeResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return TimeResponseDto.from(responseCode, responseMessage, topTimeResponseDtoList);
    }

    @Override
    public TimeResponseDto findMonthlyTime() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 최고 재생시간 top 5
        List<VideoMonthlyStatisticsEntity> timeEntityList = videoMonthlyStatisticsRepository.findTimeTop5(firstDayOfMonth, lastDayOfMonth);
        List<TopTimeResponseDto> topTimeResponseDtoList = new ArrayList<>();
        for (VideoMonthlyStatisticsEntity timeEntity : timeEntityList) {
            topTimeResponseDtoList.add(new TopTimeResponseDto(timeEntity.getVideoId(), timeEntity.getPlayTime()));
        }

        if (topTimeResponseDtoList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_STATS;
            responseMessage = ResponseMessage.NOT_EXIST_STATS;
        }

        return TimeResponseDto.from(responseCode, responseMessage, topTimeResponseDtoList);
    }
}
