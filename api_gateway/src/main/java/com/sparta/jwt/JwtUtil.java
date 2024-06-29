package com.sparta.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Token 식별자
    public static final String BEAR = "Bearer ";

    // JWT secret key
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key secret;

    @PostConstruct
    public void init() {
        // Base64 디코딩하여 byte 배열로 변환
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        secret = new SecretKeySpec(decodedKey, "HmacSHA256");
    }


    // JWT 토큰에서 클레임을 추출하는 메서드
    public Mono<Claims> getClaimsFromToken(String token) {
        if (token != null && token.startsWith(BEAR)) {
            token = token.substring(7).trim();
        }

        String finalToken = token;
        return Mono.defer(() -> {
            try {
                log.info("JWT 토큰에서 클레임을 추출합니다.");

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secret)
                        .build()
                        .parseClaimsJws(finalToken)
                        .getBody();
                return Mono.just(claims);
            } catch (Exception e) {
                log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
                log.error("유효하지 않은 JWT 토큰: {}", finalToken);
                return Mono.error(new RuntimeException("Invalid JWT token"));
            }
        }).onErrorResume(e -> {
            if (e instanceof RuntimeException) {
                return Mono.error(e); // 이미 RuntimeException 이면 그대로 반환
            } else {
                return Mono.error(new RuntimeException("Unexpected error occurred", e));
            }
        });
    }

    // HTTP 요청 헤더에서 JWT 토큰을 추출하는 메서드
    public String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEAR)) {
            log.info("HTTP 요청 헤더에서 JWT 토큰을 추출합니다. {}", bearerToken);
            return bearerToken.substring(7);
        }
        return null;
    }

    // JWT 토큰을 기반으로 인증 정보를 설정하는 메서드
    public void setAuthentication(String token) {
        if (token != null && token.startsWith(BEAR)) {
            token = token.substring(7).trim();
        }
        getClaimsFromToken(token).subscribe(claims -> {
            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            log.info("JWT 토큰에서 사용자 정보를 추출하여 인증을 설정합니다. 사용자: {}, 권한: {}", username, roles);

            // 사용자 정보를 UserDetails 객체로 변환
            UserDetails userDetails = User.builder()
                    .username(username)
                    .password("") // 비밀번호는 필요하지 않음
                    .authorities( roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+ role)).collect(Collectors.toList()))
                    .build();

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // SecurityContext 에 인증 객체 설정
            ReactiveSecurityContextHolder.withAuthentication(authentication);
        });
    }

    public String getUserIdFromToken(String token) {
        if (token != null && token.startsWith(BEAR)) {
            token = token.substring(7).trim();
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String accessToken) {
        return validateTokenInternal(accessToken);
    }
    // 토큰 검증 공통 로직
    private boolean validateTokenInternal(String token) {
        try {
            if (token != null && token.startsWith(BEAR)) {
                token = token.substring(7).trim();
            }
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
            throw e;
        } catch (Exception e){
            log.error("잘못되었습니다.", e);
            throw e;
        }
    }
}
