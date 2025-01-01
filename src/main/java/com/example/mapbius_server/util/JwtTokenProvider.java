package com.example.mapbius_server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final JwtUtil jwtUtil;

    public JwtTokenProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // JWT에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.validateToken(token); // JWT 검증 및 클레임 추출
        String id = claims.getSubject(); // 토큰의 주인 (사용자명)

        // 역할 정보 추출
        String role = claims.get("role", String.class);

        // 권한 설정
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        // 사용자 정보를 기반으로 UserDetails 생성
        UserDetails userDetails = new User(id, "", authorities);

        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }


    private final String secretKey = "my-secret-key"; // 환경 변수로 관리하는 것이 안전
    private final long expirationTime = 3600000; // 1시간 (밀리초)

    public String createToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId) // 사용자 ID
                .claim("role", "ROLE_USER") // 사용자 역할 (예제)
                .setIssuedAt(now) // 생성 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명
                .compact();
    }

    // 토큰 검증 및 클레임 추출
    public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }









}
