package com.example.mapbius_server.util;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // 누구든지 api 요청시 해당 필터가 가장 먼저 동작하여 토큰 유효성 검사
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil; // JwtUtil 주입, 토큰이 진짜인지 검사
    private JwtTokenProvider jwtTokenProvider; // 티켓을 보고 누구인지 알려주는 도구.

    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtTokenProvider jwtTokenProvider) {
        this.jwtUtil = jwtUtil;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    // request로 사용자가 보낸 Authorization 헤더 확인. (Jwt 토큰, 게시글 제목, 내용, 사용자 정보 조회 등)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        /**
         * resolveToken 동작
         */
        String token = resolveToken(request); // 요청에서 JWT 토큰 추출

        if (token != null) {
            try {
                // 토큰 검증
                jwtUtil.validateToken(token); // 유효하지 않으면 예외 발생
                Authentication auth = jwtTokenProvider.getAuthentication(token); // 토큰에서 사용자 정보 추출 및 인증 생성
                SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContext에 인증 정보 설정
            } catch (JwtException e) {
                // 유효하지 않은 토큰 처리
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response); // 사용자가 보낸 토큰의 검증이 성공하면 다음 단계로 넘김.
    }


    /**
     * resolveToken Method
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
