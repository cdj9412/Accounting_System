package com.sparta.dto.response;

import com.sparta.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogOutResponseDto  {
    private String user_id;

    public LogOutResponseDto(String user_id) {
        this.user_id = user_id;
    }
}
