package com.sparta.dto.response;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PlayResponseDto {
    private String userId; // 재생 이용자
    private Long videoId; // 재생 이용자
    private int startPoint; // 시작 지점
    private boolean abusing; // 어뷰징 체크

    public PlayResponseDto(String userId, Long videoId, int startPoint, boolean abusing) {
        this.userId = userId;
        this.videoId = videoId;
        this.startPoint = startPoint;
        this.abusing = abusing;
    }
}
