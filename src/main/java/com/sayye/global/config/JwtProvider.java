package com.sayye.global.config;

import com.sayye.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JwtProvider {
    // HTTP 헤더 상수
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    // HS256 알고리즘은 최소 256비트(32바이트) 키가 필요
    // production 환경에선 환경변수로 관리 권장
    private final SecretKey secretKey;
    private final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 1주
    
    // 로그아웃된 토큰 블랙리스트 (메모리 기반, 서버 재시작 시 초기화됨)
    // production 환경에서는 Redis 등 영구 저장소 사용 권장
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

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
            log.warn(ErrorCode.TOKEN_BLACKLISTED.name(), ErrorCode.TOKEN_BLACKLISTED.getMessage());
            return false;
        }
        
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn(ErrorCode.TOKEN_EXPIRED.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn(ErrorCode.TOKEN_UNSUPPORTED.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn(ErrorCode.TOKEN_MALFORMED.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn(ErrorCode.TOKEN_SIGNATURE_INVALID.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn(ErrorCode.TOKEN_ILLEGAL_ARGUMENT.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 예상치 못한 오류가 발생했습니다.", e);
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn(ErrorCode.TOKEN_EXPIRED.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn(ErrorCode.TOKEN_UNSUPPORTED.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn(ErrorCode.TOKEN_MALFORMED.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn(ErrorCode.TOKEN_SIGNATURE_INVALID.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn(ErrorCode.TOKEN_ILLEGAL_ARGUMENT.getMessage());
            throw e;
        }
    }
    
    // refreshToken인지 확인
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (ExpiredJwtException e) {
            log.debug("만료된 토큰의 타입 확인 시도 (refreshToken)");
            return false;
        } catch (Exception e) {
            log.debug("토큰 타입 확인 실패 (refreshToken): {}", e.getMessage());
            return false;
        }
    }
    
    // accessToken인지 확인
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "access".equals(claims.get("type", String.class));
        } catch (ExpiredJwtException e) {
            log.debug("만료된 토큰의 타입 확인 시도 (accessToken)");
            return false;
        } catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }

}
