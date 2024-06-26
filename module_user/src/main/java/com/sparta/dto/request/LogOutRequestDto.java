package com.sparta.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogOutRequestDto {
    @NotBlank
    private String id;

    @NotBlank
    private String accessToken;
}
