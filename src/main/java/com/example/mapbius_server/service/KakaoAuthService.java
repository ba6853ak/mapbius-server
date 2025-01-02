package com.example.mapbius_server.service;

import com.example.mapbius_server.mapper.UserMapper;
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

@RequiredArgsConstructor // AutoWired를 안 쓰고 스프링이 자동으로 의존성을 주입해줌.
public class KakaoAuthService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final UserService userService;


    // 카카오 인증 코드를 받고 동작
    public String processKakaoLogin(String authorizationCode) {
        // 1. 카카오에서 AccessToken 발급
        String tokenUri = "https://kauth.kakao.com/oauth/token"; // 카카오의 토큰 발급 앤드포인트


        // HTTP 요청의 헤더 정보를 저장하는 객체 -> HttpHeaders
        // 데이터를 url-인코딩 양식으로 보낸다
        // 요청에 포함할 메타 데이터
        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP 요청의 키-값 형식의 데이터를 저장하는 객체
        // 요청에 포함할 싩제 데이터
        // 바디 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code"); // OAuth 2.0의 기본적인 인증 유형
        body.add("client_id", "0624842a8b478e278f6669ab5bc1a6c5"); // Kakao REST API 키
        body.add("redirect_uri", "http://localhost:3000/"); // 카카오가 인증 후 반환할 URL
        body.add("code", authorizationCode); // 웹으로부터 받은 코드

        //  HttpEntity는 Java에서 HTTP 요청의 헤더와 바디를 한 번에 묶어 관리하는 객체
        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // tokenUri로 HttpEntity 인스턴스인 request를 보내서 서버에서 받은 응답을 Map으로 변환한다.

        // Kakao에서 본문에 데이터를 실어 보내준다.
        // 이 시점에서 5분동안 유효한 코드의 생명주기가 끝난다.
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);
        // (요청을 보낼 서버의 URL, 요청 데이터, 서버 응답 데이터를 변환할 타입)
        System.out.println(response.getBody());
        // restTemplate의 역할
        // 1. 요청할 URL와 데이터 준비
        // 2. RestTemplate가 요청을 해당 서버로 보낸다.
        // 3. 서버에서 보낸 응답을 받아 JAVA 객체로 변환
        // ResponseEntity<Map> -> map 방식으로 결과값을 받음.

        /**
         * {
         *   "access_token": "AAA123...",
         *   "token_type": "bearer",
         *   "expires_in": 3600,
         *   "refresh_token": "BBB456...",
         *   "scope": "profile"
         * }
         */

        // response에서 액세스 토큰을 추출.
        String accessToken = (String) response.getBody().get("access_token");

        // 2. 카카오 사용자 정보 요청
        // 카카오로부터 발급 받은 access 토큰을 사용하여 정보를 요청한다.
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userHeaders);

        // 요청 객체를 생성 후, 이를 사용해 서버에 요청을 보낸다.
        // restTemplate는 택배기사에 비유할 수 있다. 보내고 받고 하기 때문.

        // 요청을 보낼 서버의 URL, HTTP 메서드, 요청 데이터, 서버 응답을 반환할 타입)
        // exchange는 더 유연하게 데이터를 처리할 수 있다.
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                Map.class
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");

        System.out.println("userInfo: " + userInfo);
        String kakaoId = String.valueOf(userInfo.get("id")); // 카카오 ID 반환
        String kakaoNickName = String.valueOf(properties.get("nickname")); // 카카오 닉네임 반환
        System.out.println("kakaoNickName: " + kakaoNickName);

        // 이미 존재하는 회원인지 체크
        if(userService.isIdAvailable(kakaoId)) {
            System.out.println("카카오로 가입한 계정입니다.");
            // 3. JWT 생성 후 반환
            return jwtUtil.generateTokenWithRole(kakaoId, "ROLE_USER", kakaoNickName);
        } else {
            System.out.println("카카오로 가입한 정보가 없습니다.");
            return "registNeedKakaoAccount";
        }




    }
}
