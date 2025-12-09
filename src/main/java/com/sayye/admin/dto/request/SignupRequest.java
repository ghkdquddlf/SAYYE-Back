package com.sayye.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private final String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

    @NotBlank(message = "이름은 필수입니다.")
    private final String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private final String email;

}
