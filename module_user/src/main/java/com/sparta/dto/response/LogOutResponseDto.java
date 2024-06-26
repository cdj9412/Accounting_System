package com.sparta.dto.response;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LogOutResponseDto extends ResponseDto {
    private LogOutResponseDto() {
        super();
    }

    // 성공
    public static ResponseEntity<LogOutResponseDto> success (){
        LogOutResponseDto responseBody = new LogOutResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    // 로그인 이용자 아님
    public static ResponseEntity<ResponseDto> logoutError(){
        ResponseDto responseBody = new ResponseDto(ResponseCode.LOGOUT_ERROR, ResponseMessage.LOGOUT_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    // 이용자 없음
    public static ResponseEntity<ResponseDto> notExistId (){
        ResponseDto responseBody = new ResponseDto(ResponseCode.DO_NOT_EXIST_USER, ResponseMessage.DO_NOT_EXIST_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

}
