package com.sparta.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteResponseDto {
    private String userId; // 재생 이용자
    private Long videoId; // 재생 이용자

    public CompleteResponseDto(String userId, Long videoId) {
        this.userId = userId;
        this.videoId = videoId;
    }
}
