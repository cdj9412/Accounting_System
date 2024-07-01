package com.sparta.controller;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.dto.request.CompleteRequestDto;
import com.sparta.dto.request.PlayRequestDto;
import com.sparta.dto.request.StopRequestDto;
import com.sparta.dto.response.CompleteResponseDto;
import com.sparta.dto.response.PlayResponseDto;
import com.sparta.dto.response.ResponseDto;
import com.sparta.dto.response.StopResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import com.sparta.service.StreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stream")
@Slf4j(topic = "StreamingController")
public class StreamingController {
    private final StreamingService streamingService;

    // header test code
    @PostMapping("/test")
    public String test(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("Header Name: {}, Header Value: {}", headerName, headerValue);
        }
        return "test complete";
    }

    // 동영상 재생
    @PostMapping("/play")
    public ResponseEntity<ResponseDto<PlayResponseDto>> play (@RequestBody PlayRequestDto requestBody, HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        requestBody.setUserId(userId);

        PlayResponseDto playResponseDto = streamingService.play(requestBody);
        ResponseDto<PlayResponseDto> responseDto = new ResponseDto<>();

        // 성공은 맞지만 어뷰징 관련 코드와 메시지 전달
        if(playResponseDto.isAbusing()){
            responseDto.setResponseCode(ResponseCode.ABUSING_CHECK);
            responseDto.setResponseMessage(ResponseMessage.ABUSING_CHECK);
            responseDto.setData(playResponseDto);
        }
        else {
            responseDto.setResponseCode(ResponseCode.SUCCESS);
            responseDto.setResponseMessage(ResponseMessage.SUCCESS);
            responseDto.setData(playResponseDto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 동영상 중단
    @PostMapping("/stop")
    public ResponseEntity<ResponseDto<StopResponseDto>> stop (@RequestBody StopRequestDto requestBody, HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        requestBody.setUserId(userId);
        StopResponseDto stopResponseDto = streamingService.stop(requestBody);
        ResponseDto<StopResponseDto> responseDto = new ResponseDto<>();

        switch (stopResponseDto.getResult()) {
            case SUCCESS: {
                responseDto.setResponseCode(ResponseCode.SUCCESS);
                responseDto.setResponseMessage(ResponseMessage.SUCCESS);
            }
            break;
            case CONTENT: {
                responseDto.setResponseCode(ResponseCode.CONTENT_ERROR);
                responseDto.setResponseMessage(ResponseMessage.CONTENT_ERROR);
            }
            break;
            case DB: {
                responseDto.setResponseCode(ResponseCode.DATABASE_ERROR);
                responseDto.setResponseMessage(ResponseMessage.DATABASE_ERROR);
            }
            break;
        }
        responseDto.setData(stopResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 동영상 완료
    @PostMapping("/complete")
    public ResponseEntity<ResponseDto<CompleteResponseDto>> complete (@RequestBody CompleteRequestDto requestBody, HttpServletRequest request) {
        // 헤더에서 이용자 ID 가져와서 세팅
        String userId = request.getHeader("userId");
        requestBody.setUserId(userId);
        CompleteResponseDto completeResponseDto = streamingService.complete(requestBody);
        ResponseDto<CompleteResponseDto> responseDto = new ResponseDto<>();

        if(completeResponseDto.isCheckComplete()){
            responseDto.setResponseCode(ResponseCode.SUCCESS);
            responseDto.setResponseMessage(ResponseMessage.SUCCESS);
            responseDto.setData(completeResponseDto);
        }
        else {
            responseDto.setResponseCode(ResponseCode.DATABASE_ERROR);
            responseDto.setResponseMessage(ResponseMessage.DATABASE_ERROR);
            responseDto.setData(completeResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
