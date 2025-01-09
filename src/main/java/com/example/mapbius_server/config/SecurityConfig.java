package com.example.mapbius_server.config;

import com.example.mapbius_server.util.JwtAuthenticationFilter;
import com.example.mapbius_server.util.JwtTokenProvider;
import com.example.mapbius_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 추가


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // csrf 활성화
        httpSecurity
                .csrf(csrf -> csrf.disable()) // csrf 비활성화
                .cors(cors -> cors.configurationSource(request -> {
                    org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000")); // React 클라이언트 도메인 허용
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 메서드
                    config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
                    config.setAllowCredentials(true); // 인증 정보 허용
                    return config;
                }))
                // .cors(cors -> cors.disable()) // CORS 활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll() // 일반
                        .requestMatchers("/oauth/kakao/**").permitAll() // 카카오 로그인
                        .requestMatchers("/api/private/**").authenticated() // 토큰 전용
                        .requestMatchers("/uploads/profiles/**").permitAll() // 개인 프로필 이미지
                        .anyRequest().authenticated()

                ).addFilterBefore(new JwtAuthenticationFilter(jwtUtil, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // JwtTokenProvider 전달



                // .authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").authenticated()); // jwt 인증 통과

        return httpSecurity.build();

    }


    // 추가! WebSecurityCustomizer (Spring Security 6.x 이전)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/oauth/kakao/**"  // 여기를 필터체인 무시
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




}
