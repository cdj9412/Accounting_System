package com.sparta.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.jwt.JwtAuthenticationFilter;
import com.sparta.jwt.JwtAuthorizationFilter;
import com.sparta.jwt.JwtUtil;
import com.sparta.repository.UserRepository;
import com.sparta.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configurable               // bean 을 등록할 수 있도록 만들어주는 것
@Configuration              // bean method 를 가지고 있는 클래스라고 명시
@EnableWebSecurity          // web security 설정이다 명시
@RequiredArgsConstructor    // 의존성 주입 제어역전
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, userRepository);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // httpSecurity 객체를 받아서 SecurityFilterChain 반환해주는 bean
        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //세션 설정 : STATELESS 세션 유지 하지 않겠다는 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        //HTTP Basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);


        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("**", "/api/user/**").permitAll()
                .anyRequest().authenticated());

        // 예외처리 발생 시 반환 세팅
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new FailedAuthenticationEntryPoint())); // 인증에 실패하면 반환 값을 전달한다

        // 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}

// 인증 실패 시 처리
class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 권한 없음 오류 반환
        response.getWriter().write("{\"code\": \"NP\", \"message\": \"No Permission.\"}");


    }
}
