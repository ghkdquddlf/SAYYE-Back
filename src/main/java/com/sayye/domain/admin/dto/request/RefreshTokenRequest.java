package com.sayye.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

}
