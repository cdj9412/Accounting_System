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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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
        Map<String,String> data =userService.login(request);
        String accessToken = data.get("accessToken");
        String userId = data.get("userId");
        log.error("로그인 호출");
        log.info("Access token: {}", data.get("accessToken"));
        log.info("user Id: {}", userId);

        // 응답 헤더에 액세스 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        headers.add("userId", userId);

        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("accessToken", accessToken);
        tokenInfo.put("userId", userId);

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

    // 이용자 Id 통해 새로운 access 토큰 제공 - 다른 서비스에서 요청할 데이터
    @PostMapping("/refresh")
    public String getNewAccessToken(@RequestHeader("userId") String userId) {
        try {
            log.info("oldToken: {}", userId);
            UserEntity user  = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("refresh User Not Found" + userId));

            String newToken = jwtUtil.refreshAccessToken(user.getRefreshToken());
            log.error("신규 발급 토큰 : {}", newToken);
            return newToken; // 갱신된 토큰을 반환.
        }
        catch (ExpiredJwtException e) {
            return "expired token";
        }
        catch (JwtException | IllegalArgumentException e) {
            // 갱신 실패 시 에러 응답
            return "Token refresh failed!!!";
        }
    }

    @GetMapping("/{userId}")
    public UserDetails getUserInfoByUserId(@PathVariable String userId) {
        // 사용자 정보를 DB 에서 조회
        UserEntity user = userService.getUserByUserId(userId);


        // UserDetails 객체로 변환하여 반환
        return new User(
                user.getUserId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }


}
