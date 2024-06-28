package com.sparta.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// 인증 실패 시 처리
@Component
@Slf4j(topic = "FailedAuthenticationEntryPoint")
@RequiredArgsConstructor
public class FailedAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        log.error("Auth 에러 핸들링 entry point: {}", e.getMessage(), e);
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "Access Denied");
        errorDetails.put("detail", e.getMessage());

        byte[] errorResponse;
        try {
            errorResponse = objectMapper.writeValueAsString(errorDetails).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ex) {
            log.error("Error writing JSON output", ex);
            errorResponse = "{\"error\":\"Internal Server Error\",\"message\":\"Unable to process JSON\"}".getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(errorResponse);
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
