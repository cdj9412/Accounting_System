package com.sparta.service.impl;

import com.sparta.dto.request.PlayRequestDto;
import com.sparta.dto.response.PlayResponseDto;
import com.sparta.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StreamingServiceImplement implements StreamingService {

    // 동영상 재생
    @Override
    public ResponseEntity<? super PlayResponseDto> play(PlayRequestDto playRequestDto) {
        return null;
    }
}
