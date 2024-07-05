package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ViewResponseDto {
    private String responseCode;
    private String responseMessage;
    private List<TopViewResponseDto> topViewList;

    public static ViewResponseDto from(String responseCode, String responseMessage, List<TopViewResponseDto> topViewList) {
        return ViewResponseDto.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .topViewList(topViewList)
                .build();
    }
}
