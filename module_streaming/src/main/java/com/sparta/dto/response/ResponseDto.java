package com.sparta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j(topic = "ResponseDto")
public class ResponseDto {
    private String code;
    private String message;
}
