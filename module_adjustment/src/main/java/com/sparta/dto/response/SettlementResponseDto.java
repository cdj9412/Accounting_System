package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SettlementResponseDto {
    private String responseCode;
    private String responseMessage;
    private List<SettlementDto> settlementList;

    public static SettlementResponseDto from(String responseCode, String responseMessage, List<SettlementDto> settlementList) {
        return SettlementResponseDto.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .settlementList(settlementList)
                .build();
    }
}
