package org.shaloman.pj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정.
 * - CSRF 비활성화 (REST API)
 * - 세션 무상태 (STATELESS)
 * - JWT 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
 * - 공개 엔드포인트: 로그인, Swagger(dev만), 정적 리소스
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // 공개 엔드포인트
                auth.requestMatchers("/api/auth/login").permitAll();
                // 회원가입(POST /api/users)은 인증 필요 — 관리자만 사용자 등록 (취약점 #6)
                // Swagger UI 접근 허용 (운영 환경 포함)
                auth.requestMatchers("/swagger-ui/**").permitAll();
                auth.requestMatchers("/swagger-ui.html").permitAll();
                auth.requestMatchers("/v3/api-docs/**").permitAll();
                auth.requestMatchers("/uploads/**").permitAll();
                // PBS API — 공개 접근 허용 (Confluence 스타일 콘텐츠)
                auth.requestMatchers("/api/pbs/**").permitAll();
                // 나머지 /api/** 는 인증 필요
                auth.requestMatchers("/api/**").authenticated();
                // 그 외 모든 요청 허용
                auth.anyRequest().permitAll();
            })
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new RoleAuthorizationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 취약점 #2: CORS 제한 — 특정 origin만 허용
        config.setAllowedOriginPatterns(List.of(
            "http://localhost",
            "http://localhost:80",
            "http://localhost:38080",
            "http://123.143.232.180",
            "http://123.143.232.180:38080",
            "http://192.168.2.15",
            "http://192.168.2.15:38080"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}