package com.sparta.dto.response;

import com.sparta.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto {
    private String userId;
    private String email;
    private String role;

    public SignUpResponseDto(UserEntity user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

}
