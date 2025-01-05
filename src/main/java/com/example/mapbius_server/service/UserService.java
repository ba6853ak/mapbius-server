package com.example.mapbius_server.service;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;

import com.example.mapbius_server.dto.JoinRequest;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.util.JwtTokenProvider;
import com.example.mapbius_server.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private JwtTokenProvider jtp;

    public UserService(UserMapper userMapper, JwtTokenProvider jtp) {
        this.userMapper = userMapper;
        this.jtp = jtp;
    }


    public List<User> getAllUsers() {
        return userMapper.findAll();

    }

    public boolean insertUser(User getUser) {

        User setUser = new User();
        setUser.setId(getUser.getId());
        setUser.setPw(getUser.getPw());
        setUser.setNickName(getUser.getNickName());
        setUser.setEmail(getUser.getEmail());
        setUser.setBirthDate(getUser.getBirthDate());
        setUser.setGender(getUser.getGender());
        System.out.println(getUser);

        try {
            userMapper.insertUser(setUser);
            System.out.println(setUser);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }


    public boolean registKakaoUser(User getUser, String authorizationHeader) {
        // JWT 토큰 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authorizationHeader = authorizationHeader.substring(7); // "Bearer " 제거
        } else {
            System.out.println("Authorization 헤더가 유효하지 않습니다.");
            return false; // 헤더가 올바르지 않을 경우 처리
        }
        System.out.println("authorizationHeader: " +authorizationHeader);

        // JWT 토큰 디코드 - kakaoId, kakaoEmail 추출
        Claims claims = jtp.validateToken(authorizationHeader);
        String kakaoId = claims.getSubject(); // 토큰의 주인 (카카오 아이디)
        String kakaoEmail = claims.get("email", String.class);

        User user = new User();
        // 유효성 검사
        isValidEmail(kakaoEmail);
        isValidBirthday(getUser.getBirthDate());
        isValidGender(getUser.getGender());

        System.out.println("kakaoEmail: " + kakaoEmail);
        user.setId(kakaoId);
        user.setEmail(kakaoEmail);
        user.setKakaoId(kakaoId);
        user.setNickName(getUser.getNickName());
        user.setBirthDate(getUser.getBirthDate());
        user.setGender(getUser.getGender());
        if(userMapper.insertKakaoUser(user) > 0){
            return true; // 성공
        } else {
            return false; // 실패
        }
    }


    // isValidId에 종속됨.
    public boolean isIdAvailable(String userId) {
        boolean result = userMapper.selectUserId(userId) > 0; // 아이디 존재 여부.
        System.out.println("아이디 확인 동작");
        System.out.println(result);
        System.out.println(userId);
        System.out.println(userMapper.selectUserId(userId));
        if (result) {
            return false; // 아이디가 이미 존재하여 이용할 수 없음.
        } else {
            return true; // 아이디가 존재하지 않아 이용할 수 있음.
        }
    }

    // isValidId에 종속됨.
    public boolean isKakaoIdAvailable(String kakaoId) {
        boolean result = userMapper.selectKakaoId(kakaoId) > 0; // 아이디 존재 여부.
        logger.info("카카오 계정으로 회원가입 되었는지 검사중");
        System.out.println(result);
        System.out.println(kakaoId);
        System.out.println(userMapper.selectKakaoId(kakaoId));
        if (result) {
            logger.info("카카오 아이디 존재가 확인됨.");
            return false; // 아이디가 이미 존재하여 이용할 수 없음.
        } else {
            logger.info("카카오 아이디가 존재하지 않음.");
            return true; // 아이디가 존재하지 않아 이용할 수 있음.
        }
    }




    public boolean isEmailAvailable(String setEmail) {
        boolean result = userMapper.selectUserEmail(setEmail) > 0; // 이메일 존재 여부.
        if (result) {
            return false; // 이메일이 이미 존재하여 이용할 수 없음.
        } else {
            return true; // 이메일이 존재하지 않아 이용할 수 있음.
        }
    }

/*    public String findPw(User user) {




    }*/





    public boolean isNickNameAvailable(String setNickName) {
        boolean result = userMapper.selectUserNickName(setNickName) > 0; // 이메일 존재 여부.
        if (result) {
            return false; // 이메일이 이미 존재하여 이용할 수 없음.
        } else {
            return true; // 이메일이 존재하지 않아 이용할 수 있음.
        }
    }

    /**
     * 비어있는 문자열 및 null 처리
     * 길이 5~20자, 공백 불가
     * 영어 소문자, 숫자, 특수문자(-, _)만 사용 가능
     * 아이디 중복 확인
     */
    public boolean isValidId(String setId) {
        if (setId == null || setId.equals("")) {
            System.out.println("비어 있는 문자 처리");
            return false;
        }
        if ((setId.length() < 5 || setId.length() > 20)) {
            System.out.println("아이디 길이 제한");
            return false;
        }
        if (!setId.matches("^[a-z0-9_-]+$")) {
            System.out.println("문자 사용 범위 제한");
            return false;
        }
        if (!isIdAvailable(setId)) {
            System.out.println("아이디 중복 확인");
            return false;
        }
        System.out.println("ID 유효성 검사 통과 true 반환");
        return true;
    }

    /**
     * 비어있는 문자열 및 null 처리
     * 길이 8~20자, 공백 불가
     * 영어 대/소문자, 숫자, 특수문자(!, @, #, $, %, ^, &, *)만 사용 가능
     */
    public boolean isValidPw(String setPw) {
        System.out.println("입력된 비밀번호: " + setPw);
        if (setPw == null || setPw.equals("")) {
            System.out.println("비어 있는 문자 처리");
            return false;
        }
        if ((setPw.length() < 8 || setPw.length() > 20)) {
            System.out.println("비밀번호 길이 제한");
            return false;
        }
        if (!setPw.matches("^[a-zA-Z0-9!@#$%^&*]+$")) {
            System.out.println("문자 허용 범위");
            return false;
        }
        System.out.println("Password 유효성 검사 통과 true 반환");
        return true;
    }

    public boolean isValidEmail(String setEmail) {
        logger.info("이메일 유효성 검사 시작");
        // 이메일 정규식 정의
        final String emailRegex =
                "(?i)^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@(([^<>()\\[\\]\\\\.,;:\\s@\"]+\\.)+[^<>()\\[\\]\\\\.,;:\\s@\"]{2,})$";
        System.out.println("입력된 이메일: " + setEmail);
        if (setEmail == null || setEmail.equals("")) {
            System.out.println("비어 있는 문자열 처리");
            return false;
        }
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


    public boolean isJoinValid(User user) {

        // 아이디 유효성 검사
        System.out.println("아이디 유효성 검사 동작");
        if (!isValidId(user.getId())) {
            return false;
        }
        System.out.println("아이디 유효성 검사 통과");

        // 비밀번호 유효성 검사
        System.out.println("비밀번호 유효성 검사 동작");
        if (!isValidPw(user.getPw())) {
            return false;
        }
        System.out.println("비밀번호 유효성 검사 통과");

        // 이메일 유효성 검사
        System.out.println("이메일 유효성 검사 동작");
        if (!isValidEmail(user.getEmail())) {
            return false;
        }
        System.out.println("이메일 유효성 검사 통과");

        // 닉네임 유효성 검사
        System.out.println("닉네임 유효성 검사 동작");
        if (!isValidNickName(user.getNickName())) {
            return false;
        }
        System.out.println("닉네임 유효성 검사 통과");

        // 생년월일 유효성 검사
        System.out.println("생년월일 유효성 검사 동작");
        if (!isValidBirthday(user.getBirthDate())) {
            return false;
        }
        System.out.println("생년월일 유효성 검사 통과");

        // 성별 유효성 검사
        System.out.println("성별 유효성 검사 동작");
        if (!isValidGender(user.getGender())) {
            return false;
        }
        System.out.println("성별 유효성 검사 통과");

        return true; // 모두 통과시 true 반환
    }

    public boolean isValidGender(String setGender) {

        if(setGender == null || setGender.equals("")) {
            System.out.println("비어 있는 문자열 처리");
            return false;
        }

        if ( !(setGender.equals("male") || setGender.equals("female")) ){
            System.out.println("female과 male만 입력 / 값 삽입 오류");
            return false;
        }
            return true;
    }




    public boolean isValidBirthday(String birthDate) {
        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();

        // yyyy-MM-dd 형식의 날짜를 검증하는 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate parsedDate;
        try {
            // 입력된 문자열을 LocalDate로 변환 (유효성 검증 포함)
            parsedDate = LocalDate.parse(birthDate, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("잘못된 생년월일 입력 형식: yyyy-MM-dd 여야 함.");
            return false;
        }

        // 년도 검증: 1900년 이상 현재 날짜 이하인지 확인
        if (parsedDate.getYear() < 1900 || parsedDate.isAfter(currentDate)) {
            System.out.println("년도는 1900년 이상, 현재 날짜 이하만 허용됨.");
            return false;
        }

        // 월 검증 및 일자 검증은 LocalDate.parse에서 이미 처리됨
        // 추가 검증은 필요하지 않음

        return true; // 모든 검증을 통과하면 true 반환
    }






}
