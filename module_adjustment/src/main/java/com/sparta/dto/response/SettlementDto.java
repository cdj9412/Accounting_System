package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SettlementDto {
    private Long videoId;
    private Long sumSettlement;
    private Long viewsSettlement;
    private Long adSettlement;
}
