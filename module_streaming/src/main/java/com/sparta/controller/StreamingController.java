package com.sparta.controller;

import jakarta.servlet.http.HttpServletRequest;
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
}
