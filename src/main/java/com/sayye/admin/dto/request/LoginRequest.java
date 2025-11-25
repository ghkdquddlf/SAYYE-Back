package com.sayye.admin.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

    @Column(nullable = false)
    private final String email;

    @Column(nullable = false)
    private final String password;

}
