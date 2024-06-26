package com.sparta.controller;

import com.sparta.dto.request.LogOutRequestDto;
import com.sparta.dto.request.SignUpRequestDto;
import com.sparta.dto.response.LogOutResponseDto;
import com.sparta.dto.response.SignUpResponseDto;
import com.sparta.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<? super SignUpResponseDto> signup (@RequestBody @Valid SignUpRequestDto requestBody) {
        ResponseEntity<? super SignUpResponseDto> response = userService.signUp(requestBody);
        return response;
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<? super LogOutResponseDto> logout(@RequestBody @Valid LogOutRequestDto requestBody) {
        ResponseEntity<? super LogOutResponseDto> response = userService.logout(requestBody);
        return response;
    }

    // 토큰 로그인 테스트
    @PostMapping("/test")
    public String test() {
        return "success";
    }


}
