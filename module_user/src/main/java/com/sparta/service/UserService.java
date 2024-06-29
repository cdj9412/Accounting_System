package com.sparta.service;

import com.sparta.dto.request.LogOutRequestDto;
import com.sparta.dto.request.SignInRequestDto;
import com.sparta.dto.request.SignUpRequestDto;
import com.sparta.dto.response.LogOutResponseDto;
import com.sparta.dto.response.SignUpResponseDto;
import com.sparta.entity.UserEntity;

import java.util.Map;

public interface UserService {
    // 회원가입
    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);

    // 로그인
    Map<String,String> login(SignInRequestDto signInRequestDto);

    // 로그아웃
    LogOutResponseDto logout(LogOutRequestDto logOutRequestDto);

    // 이용자 id를 통해 이용자 정보 추출
    UserEntity getUserByUserId(String userId);

}
