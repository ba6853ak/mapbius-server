package com.example.mapbius_server.service;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.sql.Timestamp;
import java.util.Map;

@Service

@RequiredArgsConstructor // Autowired를 없이 자동으로 의존성 주입.
public class KakaoAuthService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final LoginService loginService;
    private final LoginMapper loginMapper;

    // 카카오 인증 코드를 받고 신규/기존 회원 모두 동작.
    public String processKakaoLogin(String code) {

        String kakaoTokenURL = "https://kauth.kakao.com/oauth/token"; // 카카오에서 AccessToken 발급 받을 앤드포인트
        String userState = "guest";
        String userRole = "ROLE_USER";

        HttpHeaders headers = new HttpHeaders(); // HTTP 요청의 헤더 정보를 저장하는 객체
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 데이터를 URL-인코딩 방식으로 보낸다.

        // HTTP 요청을 할 때 키-값 형식의 데이터를 저하는 객체
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code"); // OAuth 2.0의 기본적인 인증 유형
        body.add("client_id", "0624842a8b478e278f6669ab5bc1a6c5"); // Kakao REST API 키
        body.add("redirect_uri", "http://localhost:3000/kakao-register"); // 카카오가 인증 후 반환할 URL
        body.add("code", code); // 카카오 계정 인증으로 받은 코드

        //  HTTP 요청의 헤더와 바디를 한 번에 묶어 관리하는 객체
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // kakaoTokenURL로 HttpEntity 인스턴스인 request를 보내서 카카오 토큰 인증 서버에서 받은 응답을 Map으로 변환한다.
        // (HTTP 요청을 보낼 서버의 URL, 요청 데이터, 서버 응답 데이터를 변환할 타입)

        ResponseEntity<Map> response = restTemplate.postForEntity(kakaoTokenURL, request, Map.class); // 5분간 유효한 code 생명주기 종료
        logger.info("Kakao Code 인증 성공 및 Kakao Token 반환");

        /**
         *  # restTemplate의 역할
         *      1. 요청할 URL과 보낼 데이터를 준비한다.
         *      2. RestTemplate가 요청을 해당 서버로 보낸다.
         *      3. 해당 서버의 응답을 받아 Java 객체로 변환한다.
         *      4. Map 방식으로 결과값을 받는다.
         */

        String accessToken = (String) response.getBody().get("access_token"); // 카카오에서 발급 받은 데이터에서 accessToken을 추출한다.

        HttpHeaders userHeaders = new HttpHeaders(); // HTTP 요청의 헤더 정보를 저장하는 객체
        userHeaders.add("Authorization", "Bearer " + accessToken); // 헤더에 accessToken을 담는다.

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userHeaders); // HTTP 요청을 관리하는 객체에 사용자의 토큰을 담는다.

        // exchange는 데이터를 더 유연하게 처리할 수 있다.
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange( // RestTemplate의 역할은 택배기사에 비유할 수 있다.
                "https://kapi.kakao.com/v2/user/me", // 토큰을 통해 사용자 정보를 요청할 카카오 서버
                HttpMethod.GET, // HTTP 메서드

                userInfoRequest, // 요청 데이터
                Map.class // 반환 형식
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        logger.info("Kakao Token으로 정보 요청 성공: " + userInfo);
        // Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");

        String kakaoId = String.valueOf(userInfo.get("id")); // kakaoId 반환
        String kakaoEmail = String.valueOf(kakaoAccount.get("email")); // 이메일 반환
        logger.info("인증된 kakaoId: " + kakaoId+" / "+"인증된 kakaoEmail: " + kakaoEmail);




        // 계정 존재 및 상태 체크
        if(userService.isKakaoIdAvailable(kakaoId)) { // 카카오 아이디로 가입한 계정이 존재하지 않음.
            logger.info("카카오 계정으로 등록되어 있지 않습니다.");
            return jwtUtil.generateJWTToken(kakaoId, userRole, kakaoEmail, userState);
        }
        else { // 카카오 아이디로 가입한 계정이 존재함.
            logger.info("카카오 계정으로 등록되어 있습니다.");

            // 계정 활성화 / 비활성화 여부 체크
            if(loginMapper.selectDeActivateCheck(kakaoId) > 0){ // 비활성화 상태
                userState = "deactivate";
            } else { // 활성화 상태
                userState = "activate";
            }




            // 계정 관리자 권한 체크
            if(loginService.adminCheck(kakaoId)){
                logger.info("["+kakaoId+"]"+"님은 관리자 계정입니다.");
                userRole = "ROLE_ADMIN";
            } else {
                logger.info("["+kakaoId+"]"+"님은 일반 사용자입니다.");
                userRole = "ROLE_USER";
            }
            return jwtUtil.generateJWTToken(kakaoId, userRole , kakaoEmail, userState);
        }
    }
}

