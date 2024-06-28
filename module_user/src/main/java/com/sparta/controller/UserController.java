package com.sparta.controller;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.dto.request.LogOutRequestDto;
import com.sparta.dto.request.SignInRequestDto;
import com.sparta.dto.request.SignUpRequestDto;
import com.sparta.dto.response.LogOutResponseDto;
import com.sparta.dto.response.ResponseDto;
import com.sparta.dto.response.SignUpResponseDto;
import com.sparta.entity.UserEntity;
import com.sparta.jwt.JwtUtil;
import com.sparta.repository.UserRepository;
import com.sparta.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j(topic = "UserServiceImplement")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<SignUpResponseDto>> signup (@RequestBody SignUpRequestDto requestBody) {
        SignUpResponseDto signUpResponseDto = userService.signUp(requestBody);

        ResponseDto<SignUpResponseDto> responseDto = new ResponseDto<>();
        responseDto.setResponseCode(ResponseCode.SUCCESS);
        responseDto.setResponseMessage(ResponseMessage.SUCCESS);
        responseDto.setData(signUpResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody SignInRequestDto request, HttpServletResponse response) {
        String accessToken = userService.login(request);
        //액세스 토큰 쿠키 설정
        Cookie accessTokenCookie = new Cookie("accessToken", Base64.getUrlEncoder().encodeToString(accessToken.getBytes()));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS 에서만 전송되도록 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 30); // 30분 유효

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);

        // 응답 헤더에 액세스 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, accessToken);

        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("access_token", accessToken);

        return ResponseEntity.ok().headers(headers).body(tokenInfo);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<LogOutResponseDto>> logout(@RequestBody LogOutRequestDto requestBody) {
        LogOutResponseDto logOutResponseDto = userService.logout(requestBody);

        ResponseDto<LogOutResponseDto> responseDto = new ResponseDto<>();
        responseDto.setResponseCode(ResponseCode.SUCCESS);
        responseDto.setResponseMessage(ResponseMessage.SUCCESS);
        responseDto.setData(logOutResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 토큰 유지 테스트
    @PostMapping("/test")
    public String test() {
        return "success";
    }

    // 토큰을 통해 id 제공 - 다른 서비스에서 요청할 데이터
    @PostMapping("/refresh")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String oldToken) {
        try {
            // 토큰 갱신 로직을 호출
            String userId = jwtUtil.getUserIdFromToken(oldToken);
            UserEntity user  = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found" + userId));
            String newToken = jwtUtil.refreshAccessToken(user.getRefreshToken());
            return ResponseEntity.ok(newToken); // 갱신된 토큰을 반환합니다.
        } catch (Exception e) {
            // 갱신 실패 시 에러 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token refresh failed");
        }
    }

    @GetMapping("/{userId}")
    public UserDetails getUserInfoByUserId(@PathVariable String userId) {
        // 사용자 정보를 DB 에서 조회
        UserEntity user = userService.getUserByUserId(userId);


        // UserDetails 객체로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }


}
