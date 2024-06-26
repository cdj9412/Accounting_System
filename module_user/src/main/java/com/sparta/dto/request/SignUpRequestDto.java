package com.sparta.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {
    @NotBlank
    private String id;

    @NotBlank
    @Size(min = 8, max=13, message = "비밀번호는 8~13글자이어야 합니다." )
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).+$", message = "영어 대소문자와 특수문자를 최소 1글자씩 포함해야 합니다.")
    private String password;

    @Email
    @NotBlank
    private String email;
}
