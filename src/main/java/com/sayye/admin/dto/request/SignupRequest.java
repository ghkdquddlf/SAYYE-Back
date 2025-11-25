package com.sayye.admin.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupRequest {

    @Column(nullable = false)
    private final String id;

    @Column(nullable = false)
    private final String password;

    @Column(nullable = false)
    private final String name;

    @Email
    @Column(nullable = false)
    private final String email;
}
