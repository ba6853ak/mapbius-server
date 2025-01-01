package com.example.mapbius_server.service;

import com.example.mapbius_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;


    // 카카오 인증 코드를 받고 동작
    public String processKakaoLogin(String authorizationCode) {
        // 1. 카카오에서 AccessToken 발급
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "0624842a8b478e278f6669ab5bc1a6c5"); // Kakao API 키
        body.add("redirect_uri", "http://localhost:3000/");
        body.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        String accessToken = (String) response.getBody().get("access_token");

        // 2. 카카오 사용자 정보 요청
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                Map.class
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String kakaoId = String.valueOf(userInfo.get("id"));

        // 3. JWT 생성 후 반환
        return jwtUtil.generateTokenWithRole(kakaoId, "ROLE_USER");
    }
}
