package com.sparta.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.sparta.service.StreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stream")
@Slf4j(topic = "StreamingController")
public class StreamingController {
    private final StreamingService streamingService;

    // 동영상 재생
    //@PostMapping("/play")

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
    // 동영상 중단
    //@PostMapping("/stop")

    // 동영상 완료
    //@PostMapping("/complete")
}
