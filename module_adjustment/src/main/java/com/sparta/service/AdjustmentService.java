package com.sparta.service;

import com.sparta.dto.response.TimeResponseDto;
import com.sparta.dto.response.ViewResponseDto;

public interface AdjustmentService {
    public ViewResponseDto findDailyView();

    public ViewResponseDto findWeeklyView();

    public ViewResponseDto findMonthlyView();

    public TimeResponseDto findDailyTime();

    public TimeResponseDto findWeeklyTime();

    public TimeResponseDto findMonthlyTime();

}
