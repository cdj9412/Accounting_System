package com.sparta.controller;

import com.sparta.dto.response.SettlementResponseDto;
import com.sparta.dto.response.TimeResponseDto;
import com.sparta.dto.response.ViewResponseDto;
import com.sparta.service.SettlementService;
import com.sparta.service.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adjust")
@Slf4j(topic = "AdjustmentController")
public class AdjustmentController {
    private final StatisticsService statisticsService;
    private final SettlementService settlementService;

    // 일간 통계 조회수 Top 5
    @GetMapping("/top5/daily/view")
    public ResponseEntity<ViewResponseDto> getTop5DailyView(HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        ViewResponseDto response = statisticsService.findDailyView(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 주간 통계 조회수 Top 5
    @GetMapping("/top5/weekly/view")
    public ResponseEntity<ViewResponseDto> getTop5WeeklyView(HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        ViewResponseDto response = statisticsService.findWeeklyView(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 월간 통계 조회수 Top 5
    @GetMapping("/top5/monthly/view")
    public ResponseEntity<ViewResponseDto> getTop5MonthlyView(HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        ViewResponseDto response = statisticsService.findMonthlyView(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 일간 통계 재생시간 Top 5
    @GetMapping("/top5/daily/time")
    public ResponseEntity<TimeResponseDto> getTop5DailyTime(HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        TimeResponseDto response = statisticsService.findDailyTime(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 주간 통계 재생시간 Top 5
    @GetMapping("/top5/weekly/time")
    public ResponseEntity<TimeResponseDto> getTop5WeeklyTime(HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        TimeResponseDto response = statisticsService.findWeeklyTime(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 월간 통계 재생시간 Top 5
    @GetMapping("/top5/monthly/time")
    public ResponseEntity<TimeResponseDto> getTop5MonthlyTime(HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        TimeResponseDto response = statisticsService.findMonthlyTime(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 일간 정산
    @GetMapping("/settlement/daily")
    public ResponseEntity<SettlementResponseDto> getSettlementDaily(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        SettlementResponseDto response = settlementService.findDailySettlement(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 주간 정산
    @GetMapping("/settlement/weekly")
    public ResponseEntity<SettlementResponseDto> getSettlementWeekly(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        SettlementResponseDto response = settlementService.findWeeklySettlement(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 월간 정산
    @GetMapping("/settlement/monthly")
    public ResponseEntity<SettlementResponseDto> getSettlementMonthly(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        SettlementResponseDto response = settlementService.findMonthlySettlement(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
