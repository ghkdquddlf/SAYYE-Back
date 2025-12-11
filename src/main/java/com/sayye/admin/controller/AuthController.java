package com.sayye.admin.controller;

import com.sayye.admin.config.JwtProvider;
import com.sayye.admin.dto.request.LoginRequest;
import com.sayye.admin.dto.request.RefreshTokenRequest;
import com.sayye.admin.dto.request.SignupRequest;
import com.sayye.admin.dto.response.AdminResponse;
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
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AdminResponse> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletRequest request
    ) {
        TokenPair tokens = authService.login(loginRequest, request);

        HttpHeaders headers = getHttpHeaders(tokens);

        return ResponseEntity.ok()
                .headers(headers)
                .body("로그인에 성공하였습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok()
                .body("성공적으로 로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
        HttpServletRequest httpRequest,
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenPair tokens = authService.refresh(httpRequest, request);

        HttpHeaders headers = getHttpHeaders(tokens);

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    // HTTP Headers 가져오는 메서드
    private static HttpHeaders getHttpHeaders(TokenPair tokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtProvider.AUTHORIZATION_HEADER, JwtProvider.BEARER_PREFIX + tokens.getAccessToken());
        headers.set("Refresh-Token", tokens.getRefreshToken());
        return headers;
    }

}
