package com.sparta.security;

import com.sparta.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j(topic = "JwtAuthorizationFilter_검증 및 인가")
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter implements WebFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 로그인과 관련된 경로는 필터링하지 않음
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/user/") ){
            return chain.filter(exchange);
        }
        String accessToken = jwtUtil.getJwtFromHeader(exchange.getRequest());
        if (StringUtils.hasText(accessToken)) {
            try {
                Claims claims = jwtUtil.getClaimsFromToken(accessToken);

                if (claims.getExpiration().before(new Date())) {
                    return requestNewAccessToken(exchange).flatMap(newAccessToken -> {
                        if (newAccessToken != null) {
                            String userId = jwtUtil.getClaimsFromToken(newAccessToken).getSubject();
                            return setAuthentication(userId).then(chain.filter(exchange));
                        } else {
                            return handleTokenExpirationError(exchange);
                        }
                    });
                } else {
                    return setAuthentication(accessToken).then(chain.filter(exchange));
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                return handleUnauthorizedError(exchange);
            }
        }
        return chain.filter(exchange);
    }

    private Mono<String> requestNewAccessToken(ServerWebExchange exchange) {
        return WebClient.create()
                .post()
                .uri("http://localhost:8080/api/user/refresh")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.getJwtFromHeader(exchange.getRequest()))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> setAuthentication(String user_id) {
        return Mono.defer(() -> {
            Authentication authentication = createAuthentication(user_id);
            return Mono.fromRunnable(()->ReactiveSecurityContextHolder.withAuthentication(authentication));
        });
    }

    private Authentication createAuthentication(String user_id) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user_id);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Mono<Void> handleTokenExpirationError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"Token expired\"}".getBytes())));
    }

    private Mono<Void> handleUnauthorizedError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"유효하지 않은 액세스 토큰.\"}".getBytes())));
    }
}
