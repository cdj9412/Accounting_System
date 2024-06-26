package com.sparta.service;

import com.sparta.dto.request.PlayRequestDto;
import com.sparta.dto.response.PlayResponseDto;
import org.springframework.http.ResponseEntity;

public interface StreamingService {
    // 동영상 재생
    ResponseEntity<? super PlayResponseDto> play(PlayRequestDto playRequestDto);

    // 동영상 중단 - 종료 시점 저장 용도

    // 동영상 완료 - 완전히 다 봤다는 걸 확인하는 용도
}
