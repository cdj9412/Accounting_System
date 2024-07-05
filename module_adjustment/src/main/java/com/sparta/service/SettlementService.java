package com.sparta.service;

import com.sparta.dto.response.SettlementResponseDto;

public interface SettlementService {
    SettlementResponseDto findDailySettlement(String userId);

    SettlementResponseDto findWeeklySettlement(String userId);

    SettlementResponseDto findMonthlySettlement(String userId);
}
