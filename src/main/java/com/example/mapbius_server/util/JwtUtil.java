package com.example.mapbius_server.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your-256-bit-secret-key-for-jwt12345678901234"; // 256비트 키
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2시간

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // JWT 생성
    public String generateToken(String id) {
        return Jwts.builder() // JWT 만들기 시작점
                .setSubject(id) // 사용자의 ID를 토큰의 주인으로 설정
                .setIssuedAt(new Date()) // 토근 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 토근 만료 시간
                // key는 서버만 알고 있어야 하는 열쇠이며 서명을 만들 때 사용.
                // SignatureAlgorithm.HS256은 서명 알고리즘 방식.
                // header.payload.signature 구조에서 signature 부분이 .signWith()로 생성된 부분임.
                /**
                 * 사용자가 토큰을 서버로 다시 보내면 서버는 비밀 열쇠로 서명을 다시 계산한다.
                 * 토큰의 서명과 서버가 계산한 서명이 같으면, 톸느이 진짜임을 확인한다.
                 * .claim("user-email", "john.doe@example.com") // 추가 정보 입력 메서드
                 */
                .signWith(key, SignatureAlgorithm.HS256) // 토큰에 디지털 서명을 추가함.
                .compact(); // 암호화된 토큰을 문자열로 반환.
    }

    // 로그인 시 사용
    public String generateJWTToken(String id, String role, String email, String state) {
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("role", role); // 역할 정보 추가
        claims.put("email", email); // 이메일 추가
        claims.put("state", state); // 이메일 추가
/*        if (nickName != null && !nickName.isEmpty()) {
            claims.put("nickName", nickName);
        }*/

        return Jwts.builder()
                .setSubject(id) // 사용자의 ID를 토큰의 주인으로 설정
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256) // 토큰에 디지털 서명을 추가함.
                .compact();
    }


    // JWT 검증
    public Claims validateToken(String token) {
        return Jwts.parserBuilder() // 토큰 검증 부분
                // 이 부분에서 토큰의 서명을 검증하며, 변조되었거나 유효하지 않다면 예외를 던지고 요청을 거부한다.
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
