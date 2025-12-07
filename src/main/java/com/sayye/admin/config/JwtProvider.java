package com.sayye.admin.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtProvider {
    // HS256 알고리즘은 최소 256비트(32바이트) 키가 필요
    // production 환경에선 환경변수로 관리 권장
    private final SecretKey secretKey = Keys.hmacShaKeyFor("sayyeSecretKeyForJWTTokenGenerationMustBeAtLeast32BytesLong".getBytes());
    private final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 1주
    
    // 로그아웃된 토큰 블랙리스트 (메모리 기반, 서버 재시작 시 초기화됨)
    // production 환경에서는 Redis 등 영구 저장소 사용 권장
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public String generateAccessToken(String userId, String role) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        // 블랙리스트에 있는 토큰은 무효
        if (blacklistedTokens.contains(token)) {
            return false;
        }
        
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 토큰을 블랙리스트에 추가 (로그아웃 시 사용)
    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }
    
    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // refreshToken인지 확인
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }
    
    // accessToken인지 확인
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "access".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

}
