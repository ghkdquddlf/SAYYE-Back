package com.sayye.domain.admin.service;

import com.sayye.global.config.JwtProvider;
import com.sayye.domain.admin.dto.request.LoginRequest;
import com.sayye.domain.admin.dto.request.RefreshTokenRequest;
import com.sayye.domain.admin.dto.request.SignupRequest;
import com.sayye.domain.admin.dto.response.AdminResponse;
import com.sayye.domain.admin.entity.Admin;
import com.sayye.global.exception.ApiException;
import com.sayye.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AdminService adminService;

    @Transactional
    public LoginResult login(LoginRequest loginRequest, HttpServletRequest request) {
        Admin admin = adminService.getAdminByUserId(loginRequest.getUserId());

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            throw new ApiException(ErrorCode.ADMIN_LOGIN_PASSWORD_INCORRECT);
        }

        // 기존 토큰이 있다면 무효화 (단일 세션 유지)
        String oldAccessToken = extractAccessToken(request);
        if (oldAccessToken != null && jwtProvider.validateToken(oldAccessToken)) {
            jwtProvider.invalidateToken(oldAccessToken);
        }

        String accessToken = jwtProvider.generateAccessToken(admin.getUserId(), admin.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(admin.getUserId());

        return new LoginResult(accessToken, refreshToken, admin.getRole().name());
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        // Authorization 헤더에서 accessToken 추출
        String accessToken = extractAccessToken(request);
        
        // 토큰이 없는 경우
        if (accessToken == null || accessToken.isEmpty()) {
            throw new ApiException(ErrorCode.TOKEN_INVALID);
        }
        
        // 이미 로그아웃된 유저인지 확인
        if (jwtProvider.isTokenBlacklisted(accessToken)) {
            throw new ApiException(ErrorCode.TOKEN_ALREADY_LOGGED_OUT);
        }
        
        // 토큰 유효성 검증
        if (!jwtProvider.validateToken(accessToken)) {
            throw new ApiException(ErrorCode.TOKEN_INVALID);
        }

        // 토큰을 블랙리스트에 추가하여 무효화
        jwtProvider.invalidateToken(accessToken);
    }

    @Transactional
    public TokenPair refresh(HttpServletRequest httpRequest, RefreshTokenRequest request) {
        // Authorization 헤더에서 기존 accessToken 추출
        String oldAccessToken = extractAccessToken(httpRequest);

        String refreshToken = request.getRefreshToken();

        // refreshToken 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorCode.TOKEN_INVALID);
        }

        // refreshToken 타입 확인 (accessToken이 아닌지 체크)
        if (!jwtProvider.isRefreshToken(refreshToken)) {
            throw new ApiException(ErrorCode.TOKEN_TYPE_MISMATCH);
        }

        // 이미 사용된 refreshToken인지 확인
        if (jwtProvider.isTokenBlacklisted(refreshToken)) {
            throw new ApiException(ErrorCode.ADMIN_ALREADY_LOGGED_OUT);
        }

        String userId = jwtProvider.getClaims(refreshToken).getSubject();
        Admin admin = adminService.getAdminByUserId(userId);

        // 새로운 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(admin.getUserId(), admin.getRole().name());
        String newRefreshToken = jwtProvider.generateRefreshToken(admin.getUserId());

        // 기존 refreshToken 무효화 (Refresh Token Rotation)
        jwtProvider.invalidateToken(refreshToken);
        
        // 기존 accessToken도 무효화 (있는 경우)
        if (oldAccessToken != null && !oldAccessToken.isEmpty()) {

            // 블랙리스트에 없고, accessToken 타입인지만 확인 후 블랙리스트 추가
            if (!jwtProvider.isTokenBlacklisted(oldAccessToken)) {
                try {
                    // 만료되었어도 블랙리스트에 추가 (보안 강화)
                    Claims claims = jwtProvider.getClaims(oldAccessToken);

                    if ("access".equals(claims.get("type", String.class))) {
                        jwtProvider.invalidateToken(oldAccessToken);
                    }

                } catch (Exception e) {
                    // 파싱 실패 시 무시 (잘못된 토큰)
                }
            }
        }

        return new TokenPair(accessToken, newRefreshToken);
    }

    // refresh 메서드에서 사용할 내부 클래스 (헤더에 토큰을 넣기 위한 용도)
    public static class TokenPair {

        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }

    // login 메서드에서 사용할 내부 클래스 (토큰 + role 정보)
    public static class LoginResult {

        private final String accessToken;
        private final String refreshToken;
        private final String role;

        public LoginResult(String accessToken, String refreshToken, String role) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.role = role;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getRole() {
            return role;
        }
    }

    @Transactional
    public AdminResponse signup(SignupRequest req) {
        return adminService.signup(req);
    }
    
    // Authorization 헤더에서 accessToken 추출하는 private 메서드
    private String extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(JwtProvider.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(JwtProvider.BEARER_PREFIX)) {
            return authHeader.substring(JwtProvider.BEARER_PREFIX.length());
        }

        return null;
    }

}
