package com.example.mapbius_server.controller;

import com.example.mapbius_server.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor // final 필드에 대해 자동으로 생성자를 만드는 lombok 어노테이션
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String jwtToken = kakaoAuthService.processKakaoLogin(code);
        if(jwtToken.equals("registNeedKakaoAccount")){

        } else {
            // 정상 로그인 처리.
            return ResponseEntity.ok(Map.of("token", jwtToken));
        }


        return null;
    }
}
