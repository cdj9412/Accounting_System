package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TopTimeResponseDto {
    private Long videoId;
    private Long watchTime;
}
