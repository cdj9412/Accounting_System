package com.sparta.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.security.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@Slf4j(topic = "Security")
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final FailedAuthenticationEntryPoint authenticationEntryPoint;
    public SecurityConfig(
            final JwtAuthorizationFilter jwtAuthorizationFilter, FailedAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        // 세션 비활성화 (JWT 를 사용하기 때문에)
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        // form login 비활성화
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        // WWW-Authenticate 비활성화
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        // 경로별 인가 작업
        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/api/user/**").permitAll()
                .pathMatchers("/api/stream/**").permitAll()
                .pathMatchers("/api/adjustment/**").permitAll()
                .anyExchange().authenticated());

        // 필터 추가
        http.addFilterAt(jwtAuthorizationFilter, SecurityWebFiltersOrder.AUTHORIZATION);

        // 예외처리 발생 시 반환 세팅
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }
}

