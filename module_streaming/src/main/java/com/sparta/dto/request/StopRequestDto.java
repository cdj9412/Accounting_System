package com.sparta.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StopRequestDto {
    @NotBlank
    private Long videoId;

    @NotBlank
    private int currentPosition;
}
