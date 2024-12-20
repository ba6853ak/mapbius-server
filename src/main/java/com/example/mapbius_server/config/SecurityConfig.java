package com.example.mapbius_server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // csrf 활성화
        httpSecurity
                .csrf(csrf -> csrf.disable()) // csrf 비활성화
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").permitAll().anyRequest().authenticated());
                // .authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").authenticated()); // jwt 인증 통과

        return httpSecurity.build();

    }




}
