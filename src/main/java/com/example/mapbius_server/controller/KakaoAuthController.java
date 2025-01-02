package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.service.KakaoAuthService;
import com.example.mapbius_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor // final 필드에 대해 자동으로 생성자를 만드는 lombok 어노테이션

public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    private final UserService userService;
    private final UserMapper userMapper;

    // 카카오 계정 로그인 (카카오 토큰 생성)
    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String token = kakaoAuthService.processKakaoLogin(code);

        ResponseData responseData = new ResponseData();

        if ("registNeedKakaoAccount".equals(token)) {
            responseData.setCode(404);
            responseData.setMessage("카카오 계정으로 가입된 계정이 없습니다."); // 카카오 계정 등록 필요 응답
            responseData.setObjData(token);
            return ResponseEntity
                    .status(404) // 숫자로 상태 코드 지정
                    .body(responseData);
        } else {
            responseData.setCode(200);
            responseData.setMessage("카카오 계정이 등록되어 있습니다. 로그인을 시도합니다."); // 정상 로그인 처리
            responseData.setObjData(token);
            return ResponseEntity
                    .status(200) // 숫자로 상태 코드 지정
                    .body(responseData);
        }
    }

    // 토큰 체크, 실패 시 403
    @PostMapping("/api/private/token-check")
    public ResponseEntity<?> tokenCheck(@RequestHeader("Authorization") String authorizationHeader) {
        ResponseData responseData = new ResponseData();

        responseData.setCode(200);
        responseData.setMessage("토큰 인증 성공");
        return ResponseEntity
                .status(200) // 숫자로 상태 코드 지정
                .body(responseData);
    }


    @PostMapping("/api/private/kakao/join")
    public ResponseEntity<?> kakaoAccountRegister(@RequestHeader("Authorization") String authorizationHeader, @RequestBody User user) {
        ResponseData responseData = new ResponseData();
        if(userService.registKakaoUser(user, authorizationHeader)){ // 회원가입 정보와 함께 헤더를 넘겨줌.
            responseData.setCode(200);
            responseData.setMessage("카카오 계정으로 회원가입 성공!");
            return ResponseEntity
                    .status(200) // 숫자로 상태 코드 지정
                    .body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("카카오 계정으로 회원가입 실패!");
            return ResponseEntity
                    .status(404) // 숫자로 상태 코드 지정
                    .body(responseData);
        }

    }





}
