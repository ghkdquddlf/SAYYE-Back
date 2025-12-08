package com.sayye.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // /auth/** 경로는 필터 통과
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(JwtProvider.AUTHORIZATION_HEADER);

        if (header != null && header.startsWith(JwtProvider.BEARER_PREFIX)) {
            String token = header.substring(JwtProvider.BEARER_PREFIX.length());

            if (jwtProvider.validateToken(token)) {
                Claims claims = jwtProvider.getClaims(token);

                String userId = claims.getSubject();
                String role = claims.get("role", String.class);
                
                // 권한 추가 (ROLE_ 접두사 필요)
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );
                
                User principal = new User(userId, "", authorities);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                chain.doFilter(request, response);
                return;
            }
        }

        // 토큰이 없거나 유효하지 않으면 401 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, String> errorResponse = Map.of(
            "message", "유효하지 않은 토큰입니다."
        );
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

}
