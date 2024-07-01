package com.sparta.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteResponseDto {
    private String userId; // 재생 이용자
    private Long videoId; // 재생 동영상
    private boolean checkComplete; // 동영상 시청 완료 flag

    public CompleteResponseDto(String userId, Long videoId, boolean checkComplete) {
        this.userId = userId;
        this.videoId = videoId;
        this.checkComplete = checkComplete;
    }
}
