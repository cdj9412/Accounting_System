package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TimeResponseDto {
    private String responseCode;
    private String responseMessage;
    private List<TopTimeResponseDto> topTimeList;

    public static TimeResponseDto from(String responseCode, String responseMessage, List<TopTimeResponseDto> topTimeList) {
        return TimeResponseDto.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .topTimeList(topTimeList)
                .build();
    }
}
