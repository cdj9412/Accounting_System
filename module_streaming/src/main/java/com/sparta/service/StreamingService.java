package com.sparta.service;

import com.sparta.dto.request.CompleteRequestDto;
import com.sparta.dto.request.PlayRequestDto;
import com.sparta.dto.request.StopRequestDto;
import com.sparta.dto.response.CompleteResponseDto;
import com.sparta.dto.response.PlayResponseDto;
import com.sparta.dto.response.StopResponseDto;

public interface StreamingService {
    // 동영상 재생
    PlayResponseDto play(PlayRequestDto playRequestDto);

    // 동영상 중단 - 종료 시점 저장 용도
    StopResponseDto stop(StopRequestDto stopRequestDto);

    // 동영상 완료 - 완전히 다 봤다는 걸 확인하는 용도
    CompleteResponseDto complete(CompleteRequestDto completeRequestDto);
}
