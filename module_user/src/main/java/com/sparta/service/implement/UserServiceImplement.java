package com.sparta.service.implement;

import com.sparta.dto.request.LogOutRequestDto;
import com.sparta.dto.request.SignInRequestDto;
import com.sparta.dto.request.SignUpRequestDto;
import com.sparta.dto.response.LogOutResponseDto;
import com.sparta.dto.response.SignUpResponseDto;
import com.sparta.entity.UserEntity;
import com.sparta.jwt.JwtUtil;
import com.sparta.repository.UserRepository;
import com.sparta.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UserServiceImplement")
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        // id 중복체크
        String userId = signUpRequestDto.getId();
        boolean isExistId= userRepository.existsByUserId(userId);
        if (isExistId) throw new IllegalStateException("User already exists");

        // 받아온 password 암호화
        String password = signUpRequestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        UserEntity userEntity = new UserEntity(userId, encodedPassword, signUpRequestDto.getEmail());
        UserEntity saveUserEntity = userRepository.save(userEntity);

        return new SignUpResponseDto(saveUserEntity);
    }

    // 로그인
    @Override
    public Map<String,String> login(SignInRequestDto signInRequestDto) {
        UserEntity user = userRepository.findByUserId(signInRequestDto.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if(!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword()))
            throw new IllegalStateException("Wrong password");

        // refresh token 발급 및 저장
        String refreshToken = jwtUtil.createRefreshToken(signInRequestDto.getId(), Collections.singletonList(user.getRole()));
        user.refreshTokenReset(refreshToken);
        userRepository.save(user);

        String accessToken = jwtUtil.createAccessToken(signInRequestDto.getId(), Collections.singletonList(user.getRole()));
        String userId = signInRequestDto.getId();

        Map<String,String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("userId", userId);

        return map;
    }

    // 로그아웃
    @Override
    public LogOutResponseDto logout(LogOutRequestDto logOutRequestDto) {
        // 이용자 확인
        String userId = logOutRequestDto.getId();
        UserEntity existUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        String refreshToken = existUser.getRefreshToken();
        // refresh token 삭제 후 user 데이터 저장
        existUser.refreshTokenReset("");
        userRepository.save(existUser);

        jwtUtil.invalidateToken(logOutRequestDto.getAccessToken());
        jwtUtil.invalidateToken(refreshToken);

        return new LogOutResponseDto(userId);
    }

    @Override
    public UserEntity getUserByUserId(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return user;
    }
}
