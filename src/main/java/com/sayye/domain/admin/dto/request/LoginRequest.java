package com.sayye.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private final String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

}
