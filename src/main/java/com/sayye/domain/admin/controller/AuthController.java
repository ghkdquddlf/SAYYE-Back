package com.sayye.domain.admin.controller;

import com.sayye.global.config.JwtProvider;
import com.sayye.global.response.CommonResponse;
import com.sayye.domain.admin.dto.request.LoginRequest;
import com.sayye.domain.admin.dto.request.RefreshTokenRequest;
import com.sayye.domain.admin.dto.request.SignupRequest;
import com.sayye.domain.admin.dto.response.AdminResponse;
import com.sayye.domain.admin.service.AuthService.TokenPair;
import com.sayye.domain.admin.service.AuthService.LoginResult;
import com.sayye.domain.admin.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<AdminResponse>> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.success(authService.signup(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Map<String, String>>> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletRequest request
    ) {
        LoginResult loginResult = authService.login(loginRequest, request);

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtProvider.AUTHORIZATION_HEADER, JwtProvider.BEARER_PREFIX + loginResult.getAccessToken());
        headers.set("Refresh-Token", loginResult.getRefreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(CommonResponse.success(Map.of("role", loginResult.getRole())));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(CommonResponse.success("로그아웃 되었습니다.", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<Void>> refresh(
        HttpServletRequest httpRequest,
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenPair tokens = authService.refresh(httpRequest, request);

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtProvider.AUTHORIZATION_HEADER, JwtProvider.BEARER_PREFIX + tokens.getAccessToken());
        headers.set("Refresh-Token", tokens.getRefreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(CommonResponse.success());
    }
}
