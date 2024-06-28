package com.sparta.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // httpSecurity 객체를 받아서 SecurityFilterChain 반환해주는 bean
        //csrf 보호기능 disable
        http.csrf(AbstractHttpConfigurer::disable);

        // WWW-Authenticate 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        //세션 설정 : Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .anyRequest().permitAll());

        return http.build();
    }
}
