package com.sparta.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopResponseDto {
    private String userId; // 재생 이용자
    private Long videoId; // 재생 이용자
    private int stopPoint; // 중단 시점
    private StopEnum result ; // 처리 결과

    public StopResponseDto(String userId, Long videoId, int stopPoint, StopEnum result) {
        this.userId = userId;
        this.videoId = videoId;
        this.stopPoint = stopPoint;
        this.result = result;
    }
}
