package com.sparta.entity;

import com.sparta.dto.request.SignUpRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Getter // 멤버 getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="user")
@Table(name="user")
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "social_provider")
    private String socialProvider;
    private String role;
    @Column(name = "refresh_token")
    private String refreshToken;

    public UserEntity(SignUpRequestDto signUpRequestDto) {
        this.userId = signUpRequestDto.getId();
        this.password = signUpRequestDto.getPassword();
        this.email = signUpRequestDto.getEmail();
        this.socialProvider = "app"; // 일반 가입자로 초기화
        this.role = "ROLE_USER"; // 권한 일단 모두 이용자로 초기화
    }

    @Transactional
    public void refreshTokenReset(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
