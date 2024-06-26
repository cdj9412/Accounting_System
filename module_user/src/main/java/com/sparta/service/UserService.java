package com.sparta.service;

import com.sparta.dto.request.LogOutRequestDto;
import com.sparta.dto.request.SignUpRequestDto;
import com.sparta.dto.response.LogOutResponseDto;
import com.sparta.dto.response.SignUpResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    // 회원가입
    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto signUpRequestDto);

    // 로그아웃
    ResponseEntity<? super LogOutResponseDto> logout(LogOutRequestDto logOutRequestDto);

}
