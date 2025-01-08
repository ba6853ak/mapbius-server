package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.AccountMapper;
import com.example.mapbius_server.mapper.FindMapper;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.util.JwtTokenProvider;
import com.example.mapbius_server.util.JwtUtil;
import com.example.mapbius_server.util.PasswordUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final LoginMapper loginMapper;
    private final PasswordUtil passwordUtil;
    private final LoginService loginService;
    private final UserService userService;
    private final FindMapper findMapper;
    private final KakaoAuthService kakaoAuthService;
    private final JwtUtil jwtUtil;

    public boolean kakaoAccountConnect(String header, String code){


        String header_token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims normalAccountClaims = jwtUtil.validateToken(header_token);
        normalAccountClaims.getSubject().toString();
        logger.info("기존 일반 계정 아이디: " + normalAccountClaims.getSubject().toString());

        User normalUserInfo = loginService.getUserInfo(normalAccountClaims.getSubject().toString()); // 기존 회원 정보를 DB에서 가져옵니다.


        String token = kakaoAuthService.processKakaoLogin(code);
        logger.info("Kakao login token: " + token);
        Claims kakaoAccountclaims_guest = jwtUtil.validateToken(token);
        logger.info("Kakao User Info: " + kakaoAccountclaims_guest.toString());
        String KakaoId = kakaoAccountclaims_guest.getSubject().toString();
        String KakaoEmail = kakaoAccountclaims_guest.get("email").toString();
        logger.info("기존 카카오 계정 ID: " + KakaoId);




        String KakaoAccountState = kakaoAccountclaims_guest.get("state").toString(); // 카카오 계정으로 가입 여부 체크 ( activate / guest )
        if(KakaoAccountState.equals("activate")){ // 카카오 계정으로 등록한 계정이 존재함.
            User user2 = new User();
            user2.setId(KakaoId);
            user2.setEmail(KakaoEmail);
            user2.setNickName(normalUserInfo.getNickName());
            user2.setGender(normalUserInfo.getGender());
            user2.setBirthDate(normalUserInfo.getBirthDate());

                logger.info("카카오 계정으로 Mapbius 계정 성공");
                // 사용자 데이터 옮기기.
                if(accountMapper.updateNoticesIdChange(normalUserInfo.getId(), KakaoId) > 0){
                    logger.info("공지사항 게시판 데이터 이전 성공");
                } else {
                    logger.info("공지사항 게시판 이전할 데이터가 없음.");// 공지사항 게시판 데이터 옮긺
                }
                if (userMapper.deleteUser(normalUserInfo.getId()) > 0) {
                    logger.info("데이터 이전 후 계정 강제 삭제 성공."); // 기존 계정 완전 삭제 -> 데이터 이전 완료
                }

                logger.info("기존의 일반 계정을 삭제하고 카카오 계정에 통합되었습니다.");
                return true;


        } else { // 카카오 계정으로 등록한 계정이 존재하지 않아 기존 계정의 정보로 카카오 계정을 생성함.
            User user = new User();
            user.setId(KakaoId);
            user.setEmail(KakaoEmail);
            user.setKakaoId(KakaoId);
            user.setNickName(normalUserInfo.getNickName());
            user.setGender(normalUserInfo.getGender());
            user.setBirthDate(normalUserInfo.getBirthDate());
            if(userService.NormalToKakaoUser(user, header)){
                logger.info("카카오 계정으로 Mapbius 계정 성공");
                // 사용자 데이터 옮기기.
                if(accountMapper.updateNoticesIdChange(normalUserInfo.getId(), KakaoId) > 0){
                    logger.info("공지사항 게시판 데이터 이전 성공");
                } else {
                    logger.info("공지사항 게시판 이전할 데이터가 없음.");// 공지사항 게시판 데이터 옮긺
                }
                if (userMapper.deleteUser(normalUserInfo.getId()) > 0) {
                    logger.info("데이터 이전 후 계정 강제 삭제 성공."); // 기존 계정 완전 삭제 -> 데이터 이전 완료
                }

                logger.info("일반 계정을 카카오 계정으로 전환 작업 완료");
                return true;
            }
            return false;


        }


/*        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출*/
/*        logger.info(userId);*/
        // 카카오 회원인지 판별 - users 테이블의 kakao_id 확인

/*        logger.info("뽑은 아이디: " + accountMapper.findKakaoIdByUserId(userId));
        if(accountMapper.findKakaoIdByUserId(userId) != null){ // 카카오 아이디가 존재함 ( 일반 계정과 카카오 계정의 통합이 필요함. )



            return true;
        } else { // 카카오 아이디가 존재하지 않음. ( 추가정보 입력 없이 카카오 로그인이 되도록 전환. )
            return false;
        }*/


    }




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
