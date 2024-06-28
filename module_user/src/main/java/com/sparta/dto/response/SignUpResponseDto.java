package com.sparta.dto.response;

import com.sparta.common.ResponseCode;
import com.sparta.common.ResponseMessage;
import com.sparta.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
