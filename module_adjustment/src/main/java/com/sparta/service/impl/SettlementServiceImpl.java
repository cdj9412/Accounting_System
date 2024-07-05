package com.sparta.service.impl;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.dto.response.SettlementDto;
import com.sparta.dto.response.SettlementResponseDto;
import com.sparta.entity.VideoEntity;
import com.sparta.entity.settlement.VideoDailySettlementEntity;
import com.sparta.repository.VideoRepository;
import com.sparta.repository.settlement.VideoDailySettlementRepository;
import com.sparta.service.SettlementService;
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
public class SettlementServiceImpl implements SettlementService {
    private final VideoDailySettlementRepository videoDailySettlementRepository;
    private final VideoRepository videoRepository;

    @Override
    public SettlementResponseDto findDailySettlement(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        // 이용자의 동영상 리스트 가져오기
        List<VideoEntity> userVideo = videoRepository.findByCreatorId(userId);
        List<SettlementDto> settlementList = new ArrayList<>();
        for (VideoEntity video : userVideo) {
            Long videoId = video.getId();
            // 동영상의 일일 정산 비용 가져오기
            List<VideoDailySettlementEntity> settleEntityList = videoDailySettlementRepository.findDailyData(videoId, today);
            for (VideoDailySettlementEntity settleEntity : settleEntityList) {
                Long getVideoId = settleEntity.getVideoId();
                Long getViewSettle = settleEntity.getVideoSettlementAmount();
                Long getAdSettle = settleEntity.getAdSettlementAmount();
                Long sumSettle = getViewSettle + getAdSettle;

                settlementList.add(new SettlementDto(getVideoId, sumSettle, getViewSettle, getAdSettle));
            }
        }

        if (settlementList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_SETTLE;
            responseMessage = ResponseMessage.NOT_EXIST_SETTLE;
        }

        return SettlementResponseDto.from(responseCode, responseMessage, settlementList);
    }

    @Override
    public SettlementResponseDto findWeeklySettlement(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return makeSettleResponse(userId, monday, sunday);
    }

    @Override
    public SettlementResponseDto findMonthlySettlement(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        return makeSettleResponse(userId, firstDayOfMonth, lastDayOfMonth);
    }

    private SettlementResponseDto makeSettleResponse(String userId, LocalDate startDate, LocalDate endDate) {
        String responseCode = ResponseCode.SUCCESS;
        String responseMessage = ResponseMessage.SUCCESS;

        List<VideoEntity> userVideo = videoRepository.findByCreatorId(userId);
        List<SettlementDto> settlementList = new ArrayList<>();
        for (VideoEntity video : userVideo) {
            Long videoId = video.getId();
            // 동영상의 일일 정산 비용 가져오기
            List<Object[]> settleObjectList = videoDailySettlementRepository.findPeriodData(videoId, startDate, endDate);
            for (Object[] settleObject : settleObjectList) {
                Long getVideoId = (Long)settleObject[0];
                Long getViewSettleSum = (Long)settleObject[1];
                Long getAdSettleSum = (Long)settleObject[2];
                Long sumSettle = getViewSettleSum + getAdSettleSum;

                settlementList.add(new SettlementDto(getVideoId, sumSettle, getViewSettleSum, getAdSettleSum));
            }
        }

        if (settlementList.isEmpty()) {
            responseCode = ResponseCode.NOT_EXIST_SETTLE;
            responseMessage = ResponseMessage.NOT_EXIST_SETTLE;
        }

        return SettlementResponseDto.from(responseCode, responseMessage, settlementList);

    }
}
