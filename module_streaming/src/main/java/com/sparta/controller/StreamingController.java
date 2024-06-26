package com.sparta.controller;

import com.sparta.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stream")
public class StreamingController {
    private final StreamingService streamingService;

    // 동영상 재생
    //@PostMapping("/play")

    // 동영상 중단
    //@PostMapping("/stop")

    // 동영상 완료
    //@PostMapping("/complete")
}
