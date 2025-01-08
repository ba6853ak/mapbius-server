package com.example.mapbius_server.controller;


import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.service.AccountService;
import com.example.mapbius_server.service.KakaoAuthService;
import com.example.mapbius_server.service.LoginService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginService loginService;
    private final UserService userService;
    private final KakaoAuthService kakaoAuthService;

    // 기존 일반 사용자 카카오 계정 연결
    @PostMapping("/api/private/account/kakao/connect")
    public ResponseEntity<ResponseData> kakaoConnect(@RequestHeader("Authorization") String header, @RequestBody Map<String, String> request) {
        ResponseData responseData = new ResponseData();
        String code = request.get("code");




        boolean state = accountService.kakaoAccountConnect(header, code);
        if (state) {
            responseData.setCode(200);
            responseData.setMessage("Kakao Login API에 통합되었습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("Kakao Login API에 통합 실패! - 파라미터, 유효성 검사를 확인하세요.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // 회원 정보 수정
    @PostMapping("/api/private/account/update")
    public ResponseEntity<?> accountUpdate(@RequestHeader("Authorization") String header, @RequestBody User user) {
        ResponseData responseData = new ResponseData();
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        String loginType = (String) claims.get("login_type"); // 로그인 유형
        user.setId(userId); // 토큰에서 추출한 아이디를 user 객체에 삽입.

/*        // 수정 정보 유효성 검사
        if(!userService.isValidPw(user.getPw()) && !userService.isValidEmail(user.getEmail()) && !userService.isValidNickName(user.getNickName())){
            responseData.setCode(404);
            responseData.setMessage("입력하신 정보는 유효성 검사에 위배됩니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }*/

        // 카카오 계정 정보 변경 시 유효성 검사
        if(loginType.equals("kakao")){
            if(!user.getPw().isEmpty() || !user.getPw().equals("") || !user.getEmail().isEmpty() || !user.getEmail().equals("") ){
                responseData.setCode(404);
                responseData.setMessage("수정할 수 없는 정보가 포함되어 있습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(404).body(responseData);
            }
        }


        if(accountService.updateInfo(user)){
            responseData.setCode(200);
            responseData.setMessage("개인정보 수정 성공!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("개인정보 수정 실패!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }

    }

    // (카카오 및 일반 사용자) 비밀번호 확인 후(카카오 사용자는 미확인) 회원정보 반환
    @PostMapping("/api/private/account/confirm")
    public ResponseEntity<?> accountConfirm(@RequestHeader("Authorization") String header, @RequestBody(required = false) User user) {
        ResponseData responseData = new ResponseData();
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        User getUserObj = loginService.getUserInfo(userId);

        if(user == null || user.getPw() == null || user.getPw().isEmpty()){ // 만약 카카오 사용자 일경우 PW을 받지 않음.
            logger.info("실행!");
            if(getUserObj.getKakaoId() == null || getUserObj.getKakaoId().isEmpty()){
                responseData.setCode(404);
                responseData.setMessage("카카오 계정 사용자가 아니므로 정보를 불러올 수 없습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(404).body(responseData);
            }
            logger.info(getUserObj.getId() + "님의 정보를 반환되었습니다.");
            responseData.setCode(200);
            responseData.setMessage("카카오 계정 사용자의 회원정보를 불러옵니다.");
            responseData.setObjData(getUserObj);
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(200).body(responseData);
        } else { // 만약 일반 사용자 일경우 PW을 받음
            if(accountService.confirmPw(header, user.getPw())){
                responseData.setCode(200);
                responseData.setMessage("사용자의 비밀번호가 일치하며 회원정보를 반환합니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                responseData.setObjData(getUserObj);
                return ResponseEntity.status(200).body(responseData);
            } else {
                responseData.setCode(404);
                responseData.setMessage("사용자의 비밀번호가 일치하지 않습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(404).body(responseData);
            }
        }
    }

    // (카카오 사용자) 비밀번호 확인 없이 회원정보 반환
    @PostMapping("/api/private/account/kakao/get-info")
    public ResponseEntity<?> NonAccountConfirmAndGetInfo(@RequestHeader("Authorization") String header) {
        ResponseData responseData = new ResponseData();
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        User getUserObj = loginService.getUserInfo(userId);
        if(getUserObj.getKakaoId() == null || getUserObj.getKakaoId().isEmpty()){

            responseData.setCode(404);
            responseData.setMessage("카카오 계정 사용자가 아니므로 정보를 불러올 수 없습니다.");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(404).body(responseData);
        }
        logger.info(getUserObj.getId() + "님의 정보를 반환되었습니다.");
        responseData.setCode(200);
        responseData.setMessage("카카오 계정 사용자의 회원정보를 불러옵니다.");
        responseData.setObjData(getUserObj);
        responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.status(200).body(responseData);

    }

    // 계정 삭제
    @PostMapping("/api/private/account/delete")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String header) {
        ResponseData responseData = new ResponseData();

        if(accountService.deleteAccount(header)){
            responseData.setCode(200);
            responseData.setMessage("계정 삭제 성공!");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("이미 삭제되었거나 정상적인 요청이 아닙니다.");
            return ResponseEntity.status(404).body(responseData);
        }

    }


    // 프로필 이미지 업로드
    @PostMapping("/api/private/account/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(
            @RequestPart("file") MultipartFile file, // 파일 파트
            @RequestPart("id") String userId         // 사용자 ID 파트
    ) {
        try {
            // 예: 파일과 함께 userId를 이용한 로직 처리
            accountService.saveProfileImage(userId, file);

            return ResponseEntity.ok("파일 업로드 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패: " + e.getMessage());
        }
    }






}
