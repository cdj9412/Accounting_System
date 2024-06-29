package com.sparta.security;

import com.sparta.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JwtAuthorizationFilter_검증 및 인가")
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter implements WebFilter {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 로그인과 관련된 경로는 필터링하지 않음
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/user/") ){
            return chain.filter(exchange);
        }
        String accessToken = jwtUtil.resolveToken(exchange.getRequest());
        if (StringUtils.hasText(accessToken)) {
            try {
                if(jwtUtil.validateToken(accessToken)) {
                    log.info("JWT 토큰이 유효합니다. 기존 토큰을 사용하여 인증을 설정합니다.");
                    jwtUtil.setAuthentication(accessToken);
                    return mutateExchangeWithTokenAndUserId(exchange, chain, accessToken);
                }
                else {
                    log.info("JWT 토큰이 만료되었습니다. 새로운 토큰을 요청합니다.");
                    Mono<String> result = requestNewAccessToken(exchange);
                    return result.flatMap(newAccessToken -> {
                       if(newAccessToken != null) {
                           jwtUtil.setAuthentication(newAccessToken);
                           return mutateExchangeWithTokenAndUserId(exchange, chain, newAccessToken);
                       }
                       else{
                           return handleUnauthorizedError(exchange);
                       }
                    });
                }
            }
            catch (Exception e) {
                log.error("JWT 토큰이 유효하지 않음"); ;
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"유효하지 않은 액세스 토큰.\"}".getBytes())));
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> mutateExchangeWithTokenAndUserId(ServerWebExchange exchange, WebFilterChain chain, String accessToken) {
        String userId = jwtUtil.getUserIdFromToken(accessToken); // JWT 토큰에서 userId를 추출하는 메서드

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header("userId", userId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        return chain.filter(mutatedExchange);
    }

    private Mono<String> requestNewAccessToken(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst("userId");

        return WebClient.builder().build().post()
                .uri("http://localhost:8080/api/user/refresh")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> Mono.empty())
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Token renewal request failed: {}", e.getMessage()));
    }

    private Mono<Void> handleUnauthorizedError(ServerWebExchange exchange) {
        log.error("JWT 토큰이 유효하지 않습니다.");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"유효하지 않은 액세스 토큰.\"}".getBytes())));
    }
}
