package com.sparta.dto.response;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SignUpResponseDto extends ResponseDto {
    private SignUpResponseDto() {
        super();
    }


    // 성공
    public static ResponseEntity<SignUpResponseDto> success (){
        SignUpResponseDto responseBody = new SignUpResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    // id 중복
    public static ResponseEntity<ResponseDto> duplicateId (){
        ResponseDto responseBody = new ResponseDto(ResponseCode.DUPLICATE_ID, ResponseMessage.DUPLICATE_ID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}
