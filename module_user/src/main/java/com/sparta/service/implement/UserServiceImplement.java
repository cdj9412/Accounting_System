package com.sparta.service.implement;

import com.sparta.dto.request.LogOutRequestDto;
import com.sparta.dto.request.SignUpRequestDto;
import com.sparta.dto.response.LogOutResponseDto;
import com.sparta.dto.response.ResponseDto;
import com.sparta.dto.response.SignUpResponseDto;
import com.sparta.entity.UserEntity;
import com.sparta.jwt.JwtUtil;
import com.sparta.repository.UserRepository;
import com.sparta.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    // 암호화 인터페이스
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    // 회원가입
    @Override
    @Transactional
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto signUpRequestDto) {
        try {
            // id 중복체크
            String userId = signUpRequestDto.getId();
            boolean isExistId= userRepository.existsByUserId(userId);
            if (isExistId) return SignUpResponseDto.duplicateId();

            // 받아온 password 암호화
            String password = signUpRequestDto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            signUpRequestDto.setPassword(encodedPassword);

            UserEntity userEntity = new UserEntity(signUpRequestDto);
            userRepository.save(userEntity);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignUpResponseDto.success();
    }

    // 로그아웃
    @Override
    @Transactional
    public ResponseEntity<? super LogOutResponseDto> logout(LogOutRequestDto logOutRequestDto) {
        try {
            // 이용자 확인
            String userId = logOutRequestDto.getId();
            UserEntity existUser = userRepository.findByUserId(userId);
            if (existUser == null) return LogOutResponseDto.notExistId();

            String refreshToken = existUser.getRefreshToken();
            // refresh token 삭제 후 user 데이터 저장
            existUser.refreshTokenReset("");
            userRepository.save(existUser);

            jwtUtil.invalidateToken(logOutRequestDto.getAccessToken());
            jwtUtil.invalidateToken(refreshToken);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return LogOutResponseDto.success();
    }


}
