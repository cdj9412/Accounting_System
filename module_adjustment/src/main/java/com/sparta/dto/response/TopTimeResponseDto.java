package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TopTimeResponseDto {
    private Long videoId;
    private Long dailyWatchTime;
}
