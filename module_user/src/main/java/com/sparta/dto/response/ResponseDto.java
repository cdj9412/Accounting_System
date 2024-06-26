package com.sparta.dto.response;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@Slf4j(topic = "ResponseDto")
public class ResponseDto {
    private String code;
    private String message;

    public ResponseDto() {
        this.code = ResponseCode.SUCCESS;
        this.message = ResponseMessage.SUCCESS;
    }

    //DB 에러 발생 시 반환
    public static ResponseEntity<ResponseDto> databaseError() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DATABASE_ERROR, ResponseMessage.DATABASE_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }

    // 유효성 검사 실패 시 반환
    public static ResponseEntity<ResponseDto> validationFail(Exception e) {
        log.error("유효성 검사 실패 오류 발생", e);
        ResponseDto responseBody = new ResponseDto(ResponseCode.VALIDATION_FAILED, ResponseMessage.VALIDATION_FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}
