package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.service.KakaoAuthService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequiredArgsConstructor // final 필드에 대해 자동으로 생성자를 만드는 lombok 어노테이션

public class KakaoAuthController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final KakaoAuthService kakaoAuthService;

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final LoginMapper loginMapper;

    // 카카오 계정 로그인 (카카오 토큰 생성)

    /**
     *  클라이언트에서 카카오 계정 로그인 후 코드를 서버로 받음.
     *  받은 코드를 카카오 토큰 서버와 인증하여 카카오 토큰을 받음.
     *  카카오 토큰에서 추출한 정보로 JWT에 담아 다시 클라이언트로 전송.
     */

    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> request) {

        ResponseData responseData = new ResponseData();
        String code = request.get("code");
        String token = kakaoAuthService.processKakaoLogin(code);


        Claims claims = jwtUtil.validateToken(token);
        logger.info(claims.toString());
        String state = (String) claims.get("state");


        if(state.equals("deactivate")){
            logger.info("비활성화 계정의 로그인이 차단되었습니다.");
            responseData.setCode(423);
            responseData.setMessage("비활성화 계정의 로그인이 차단되었습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(423).body(responseData);
        }


        if (state.equals("guest")) {
            // guest에서 다른 토큰으로 재생성함.
            String kakaoId = claims.getSubject();
            String kakaoEmail = claims.get("email", String.class);
            token = jwtUtil.generateJWTToken(kakaoId, "ROLE_USER", kakaoEmail, state); // 토큰을 재생성함.

            Object data = new Object();


            responseData.setCode(200);
            responseData.setMessage("카카오 계정으로 가입된 계정이 없습니다."); // 카카오 계정 등록 필요 응답
            responseData.setObjData(token);
            return ResponseEntity
                    .status(200) // 숫자로 상태 코드 지정
                    .body(responseData);
        } else {
            responseData.setCode(226);
            responseData.setMessage("카카오 계정이 등록되어 있습니다. 로그인을 시도합니다."); // 정상 로그인 처리
            responseData.setObjData(token);
            return ResponseEntity
                    .status(226) // 숫자로 상태 코드 지정
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

    // 카카오 계정으로 추가정보 입력하여 회원가입
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
