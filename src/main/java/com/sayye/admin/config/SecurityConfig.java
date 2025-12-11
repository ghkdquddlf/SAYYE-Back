package com.sayye.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity  // 추가: @PreAuthorize 사용 가능
public class SecurityConfig {

    @Autowired
    private JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtProvider);
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 인증 API - 공개
                .requestMatchers("/auth/**").permitAll()
                
                // 예약 API - 메서드별 제어
                .requestMatchers(HttpMethod.GET, "/reservations/**").permitAll()  // 조회: 누구나
                .requestMatchers(HttpMethod.POST, "/reservations/**").permitAll()  // 생성: 누구나
                .requestMatchers(HttpMethod.DELETE, "/reservations/**").permitAll()  // 취소: 전화번호 검증으로 본인 확인
                .requestMatchers(HttpMethod.PATCH, "/reservations/**").authenticated()  // 수정: 관리자만
                .requestMatchers(HttpMethod.PUT, "/reservations/**").authenticated()  // 수정: 관리자만
                
                // 방의 예약 관련 API - 학생들도 접근 가능 (구체적 경로가 우선)
                .requestMatchers(HttpMethod.GET, "/rooms/*/reservations").permitAll()  // 특정 방 예약 조회
                .requestMatchers(HttpMethod.POST, "/rooms/*/reservations").permitAll()  // 예약 생성 (학생)
                
                // 방/강좌 API - 조회만 공개, 수정은 관리자만
                .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()  // 조회: 누구나
                .requestMatchers(HttpMethod.GET, "/courses/**").permitAll()  // 조회: 누구나
                .requestMatchers("/rooms/**").authenticated()  // 방 CRUD: 관리자만
                .requestMatchers("/courses/**").authenticated()  // 강좌 CRUD: 관리자만
                
                // 관리자 API - 인증 필수
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
