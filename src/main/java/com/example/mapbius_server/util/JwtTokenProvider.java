package com.example.mapbius_server.util;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtTokenProvider {

    private final JwtUtil jwtUtil;

    public JwtTokenProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // JWT에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.validateToken(token); // JWT 검증 및 클레임 추출
        String username = claims.getSubject(); // 토큰의 주인 (사용자명)

        // 사용자 정보를 기반으로 UserDetails 생성
        UserDetails userDetails = new User(username, "", Collections.emptyList());

        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }
}
