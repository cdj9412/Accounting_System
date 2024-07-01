package com.sparta.dto.response;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
public class ResponseDto<T> {
    private String responseCode;
    private String responseMessage;
    private T data;

    public ResponseDto() {
        this.responseCode = ResponseCode.SUCCESS;
        this.responseMessage = ResponseMessage.SUCCESS;
    }
}
