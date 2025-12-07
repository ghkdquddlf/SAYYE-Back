package com.sayye.admin.controller;

import com.sayye.admin.dto.request.LoginRequest;
import com.sayye.admin.dto.request.RefreshTokenRequest;
import com.sayye.admin.dto.request.SignupRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.dto.response.LoginResponse;
import com.sayye.admin.dto.response.LogoutResponse;
import com.sayye.admin.service.AuthService.TokenPair;
import com.sayye.admin.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AdminResponse> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        TokenPair tokens = authService.login(loginRequest);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        headers.set("Refresh-Token", tokens.getRefreshToken());
        
        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
        HttpServletRequest httpRequest,
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenPair tokens = authService.refresh(httpRequest, request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        headers.set("Refresh-Token", tokens.getRefreshToken());
        
        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

}
