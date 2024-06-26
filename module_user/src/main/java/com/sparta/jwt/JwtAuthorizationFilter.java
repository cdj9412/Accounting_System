package com.sparta.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.repository.UserRepository;
import com.sparta.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "JwtAuthorizationFilter_검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    // 토큰 검증
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
            ServletException, IOException {
        String accessToken = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(accessToken)) {
            try {
                if (jwtUtil.validateToken(accessToken)) { //엑세스 토큰의 유효기간이 유효한 경우
                    String user_id = jwtUtil.getUserIdFromToken(accessToken);
                    setAuthentication(user_id);
                }
            } catch (ExpiredJwtException e) { //엑세스 토큰의 유효기간이 다 된 경우
                String user_id = e.getClaims().getSubject(); // 만료된 토큰에서 아이디 추출
                handleExpiredAccessToken(user_id, request, response, e);
            } catch (JwtException | IllegalArgumentException e) {
                handleInvalidAccessToken(response);
                return; // 에러 응답을 보낸 경우 필터 체인 중단
            }
        }

        filterChain.doFilter(request, response);
    }

    //리프레시 토큰 검증
    private void handleExpiredAccessToken(String user_id, HttpServletRequest req, HttpServletResponse res, ExpiredJwtException e) throws IOException {
        //DB 에서 리프레쉬 토큰 가져오기
        String refreshToken = userRepository.findByUserId(user_id).getRefreshToken();

        if (StringUtils.hasText(refreshToken) && jwtUtil.validateRefreshToken(refreshToken)) {
            String newAccessToken = jwtUtil.createAccessToken(user_id);

            res.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);

            setAuthentication(user_id);

            log.info("새로운 엑세스 토큰 생성 완료.");
        } else {
            sendErrorResponse(res, "유효하지 않은 리프레시 토큰.");
        }
    }

    //유효하지 않은 액세스 토큰이 들어올 경우
    private void handleInvalidAccessToken(HttpServletResponse res) throws IOException {
        sendErrorResponse(res, "유효하지 않은 액세스 토큰.");
    }

    // 인증 처리
    public void setAuthentication(String user_id) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(user_id);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String user_id) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user_id);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 에러 메시지 응답
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", ResponseCode.JWT_ERROR);
        responseData.put("message", ResponseMessage.JWT_ERROR);
        responseData.put("data", message);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
        response.getWriter().flush();
    }

}
