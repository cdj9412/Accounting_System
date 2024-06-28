package com.sparta.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j(topic = "GatewayConfig")
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                //인증 필요 없음
                .route(r-> r.path("/api/user/**")
                        .uri("lb://USER-SERVICE"))
                // 여기에 이어서 서비스 별로 추가
                .route(r-> r.path("/api/stream/**")
                        .uri("lb://STREAMING-SERVICE"))
                .build();
    }
}
