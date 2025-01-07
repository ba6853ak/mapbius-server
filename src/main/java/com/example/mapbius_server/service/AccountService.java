package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.AccountMapper;
import com.example.mapbius_server.mapper.FindMapper;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.util.JwtTokenProvider;
import com.example.mapbius_server.util.PasswordUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private FindMapper findMapper;

    // 개인 정보 수정
    public boolean updateInfo(User user) {

        String encodedPwd = "";

        // 유효성 검사
        if (user.getPw() != null && !user.getPw().isEmpty() && !user.getPw().equals("")) {
            if(userService.isValidPw(user.getPw())){
                encodedPwd = passwordUtil.encodePassword(user.getPw());
            } else {
                return false;
            }
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !user.getEmail().equals("")) {
                System.out.println(userMapper.selectUserEmail(user.getEmail()) > 0);
                if(userMapper.selectUserEmail(user.getEmail()) > 0){ // true이면 이미 존재한다.

                } else {
                    if (!isValidEmail(user.getEmail())) {
                        return false;
                    }
                }



        }

        if (user.getNickName() != null && !user.getNickName().isEmpty() && !user.getNickName().equals("")) {
            if (!isValidNickName(user.getNickName())) {
                return false;
            }
        }


        int setResult = accountMapper.updateAccount(user.getNickName(), encodedPwd, user.getEmail(), user.getId());
        logger.info("setResult : " + setResult);
        if(setResult > 0){
            return true;
        } else {
            return false;
        }

    }

    public boolean isValidNickName(String setNickName) {
        logger.info("닉네임 유효성 검사 시작");
        System.out.println("입력된 닉네임: " + setNickName);
        if (setNickName == null || setNickName.equals("")) {
            System.out.println("비어 있는 문자열 처리");
            return false;
        }
        if ((setNickName.length() < 2 || setNickName.length() > 8)) {
            System.out.println("닉네임 길이 제한");
            return false;
        }
        if (!setNickName.matches("^[가-힣a-zA-Z0-9]+$")) {
            System.out.println("닉네임 문자 제한");
            return false;
        }
/*        // 닉네임 중복 확인
        if (!isNickNameAvailable(setNickName)) {
            System.out.println("닉네임 중복 발견");
            return false;
        }*/
        logger.info("닉네임 유효성 검사 통과");
        return true;
    }


    // 비밀번호 확인
    public boolean confirmPw(@RequestHeader("Authorization") String header, String inputPw) {
        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        User userIdAndPw = loginMapper.authenticate(userId);

        if(userIdAndPw != null) {
            if(!userIdAndPw.getId().equals(userId)) {
                return false; // 만약 해당하는 ID가 있다면 TRUE
            }
            if(!passwordUtil.checkPassword(inputPw, userIdAndPw.getPw())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false; // 만약 해당하는 ID가 있다면 FALSE
        }
    }

    // 계정 탈퇴
    public boolean deleteAccount(@RequestHeader("Authorization") String header) {

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출
        int setResult = accountMapper.updateDeleteAccount(userId);
        logger.info("setResult: " + setResult);
        if(setResult > 0){
            return true;
        } else {
            return false;
        }
    }


    public boolean isValidEmail(String setEmail) {
        logger.info("이메일 유효성 검사 시작");
        // 이메일 정규식 정의
        final String emailRegex =
                "(?i)^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@(([^<>()\\[\\]\\\\.,;:\\s@\"]+\\.)+[^<>()\\[\\]\\\\.,;:\\s@\"]{2,})$";
        System.out.println("입력된 이메일: " + setEmail);
        // 이메일 길이 검사
        if (setEmail.length() > 320) {
            System.out.println("이메일 길이 제한");
            return false;
        }
        // 이메일 유효성 검사
        if (!setEmail.matches(emailRegex)) {
            System.out.println("유효하지 않은 이메일 형식");
            return false;
        }
        // 이메일 중복 확인
        if (!isEmailAvailable(setEmail)) {
            System.out.println("이메일 중복 발견");
            return false;
        }
        logger.info("이메일 유효성 검사 통과");
        return true;
    }

    public boolean isEmailAvailable(String setEmail) {
        boolean result = userMapper.selectUserEmail(setEmail) > 0; // 이메일 존재 여부.
        if (result) {
            return false; // 이메일이 이미 존재하여 이용할 수 없음.
        } else {
            return true; // 이메일이 존재하지 않아 이용할 수 있음.
        }
    }


}
