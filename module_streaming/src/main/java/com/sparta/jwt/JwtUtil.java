package com.sparta.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // JWT secret key
    @Value("${jwt.secret.key}")
    private static String secretKey;

    // JWT 토큰에서 사용자 ID 추출
    public static String extractUserId(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // 클레임의 "sub" (subject) 필드에서 사용자 ID를 추출
    }

    // 비밀 키 생성
    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
