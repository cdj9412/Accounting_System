package com.sparta.service;

import com.sparta.dto.response.TimeResponseDto;
import com.sparta.dto.response.ViewResponseDto;

public interface StatisticsService {
    ViewResponseDto findDailyView(String userId);

    ViewResponseDto findWeeklyView(String userId);

    ViewResponseDto findMonthlyView(String userId);

    TimeResponseDto findDailyTime(String userId);

    TimeResponseDto findWeeklyTime(String userId);

    TimeResponseDto findMonthlyTime(String userId);

}
