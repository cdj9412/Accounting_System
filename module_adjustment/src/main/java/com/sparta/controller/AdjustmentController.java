package com.sparta.controller;

import com.sparta.common.ResponseCode;
import com.sparta.dto.response.TimeResponseDto;
import com.sparta.dto.response.ViewResponseDto;
import com.sparta.service.AdjustmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adjust")
@Slf4j(topic = "AdjustmentController")
public class AdjustmentController {
    private AdjustmentService adjustmentService;

    // 일간 통계 조회수 Top 5
    @GetMapping("/top5/daily/view")
    public ResponseEntity<ViewResponseDto> getTop5DailyView() {
        ViewResponseDto response = adjustmentService.findDailyView();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 주간 통계 조회수 Top 5
    @GetMapping("/top5/weekly/view")
    public ResponseEntity<ViewResponseDto> getTop5WeeklyView() {
        ViewResponseDto response = adjustmentService.findWeeklyView();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 월간 통계 조회수 Top 5
    @GetMapping("/top5/monthly/view")
    public ResponseEntity<ViewResponseDto> getTop5MonthlyView() {
        ViewResponseDto response = adjustmentService.findMonthlyView();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 일간 통계 재생시간 Top 5
    @GetMapping("/top5/daily/time")
    public ResponseEntity<TimeResponseDto> getTop5DailyTime() {
        TimeResponseDto response = adjustmentService.findDailyTime();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 주간 통계 재생시간 Top 5
    @GetMapping("/top5/weekly/time")
    public ResponseEntity<TimeResponseDto> getTop5WeeklyTime() {
        TimeResponseDto response = adjustmentService.findWeeklyTime();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 월간 통계 재생시간 Top 5
    @GetMapping("/top5/monthly/time")
    public ResponseEntity<TimeResponseDto> getTop5MonthlyTime() {
        TimeResponseDto response = adjustmentService.findMonthlyTime();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
