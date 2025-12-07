package com.sayye.admin.service;

import com.sayye.admin.config.JwtProvider;
import com.sayye.admin.dto.request.LoginRequest;
import com.sayye.admin.dto.request.RefreshTokenRequest;
import com.sayye.admin.dto.request.SignupRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.dto.response.LogoutResponse;
import com.sayye.admin.entity.Admin;
import com.sayye.admin.repository.AdminRepository;
import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AdminService adminService;

    @Transactional
    public TokenPair login(LoginRequest loginRequest) {
        Admin admin = adminRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR));

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            throw new ApiException(ErrorCode.ADMIN_LOGIN_PASSWORD_INCORRECT);
        }

        String accessToken = jwtProvider.generateAccessToken(admin.getUserId(), admin.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(admin.getUserId());

        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public LogoutResponse logout(HttpServletRequest request) {
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

        return new LogoutResponse("성공적으로 로그아웃 되었습니다.");
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
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR));

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

    @Transactional
    public AdminResponse signup(SignupRequest req) {
        return adminService.signup(req);
    }
    
    // Authorization 헤더에서 accessToken 추출하는 private 메서드
    private String extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

