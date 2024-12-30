package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;

import com.example.mapbius_server.dto.JoinRequest;
import com.example.mapbius_server.mapper.UserMapper;
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

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
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


    // isValidId에 종속됨.
    public boolean isIdAvailable(String userId) {
        boolean result = userMapper.selectUserId(userId) > 0; // 아이디 존재 여부.
        if (result) {
            return false; // 아이디가 이미 존재하여 이용할 수 없음.
        } else {
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
        return true;
    }

    public boolean isValidNickName(String setNickName) {

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
        LocalDate localDate = LocalDate.now();
        int currentYear = localDate.getYear();
        int currentMonth = localDate.getMonthValue();
        int currentDay = localDate.getDayOfMonth();

        // yyyy-MM-dd 형식을 검증하는 정규식
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";

/*        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd"); // SimpleDateFormat을 사용하여 yyyy-MM-dd 형식 지정
        String formattedDate = dateFormatter.format(birthDate); // Date 객체를 문자열로 변환*/

        boolean isValid = birthDate.matches(regex); // yyyy-MM-dd 형식 검증

        // String에서 사용자가 입력한 년, 월, 일 추출
        String yearStr = birthDate.substring(0, 4);
        String monthStr = birthDate.substring(5, 7);
        String dayStr = birthDate.substring(8, 10);

        // 추출한 년, 월, 일을 int로 변환
        int yearInt = Integer.parseInt(yearStr);
        int monthInt = Integer.parseInt(monthStr);
        int dayInt = Integer.parseInt(dayStr);

        // 날짜 형식 검증
        if(!isValid || !(regex.length() != 10)) {
            System.out.println("잘못된 생년월일 입력 형식");
            System.out.println("yyyy-mm-dd 여야함.");
            return false;
        }

        // 년도 검증
        if (yearInt < 1900 || yearInt > currentYear) {
            System.out.println("1900년 이하 이거나 현재 년도를 초과함.");
            return false;
        }

        // 월 검증
        if (yearInt == currentYear) {
            // 사용자가 현재 연도를 입력한 경우
            if (monthInt > currentMonth) {
                System.out.println("현재 년도의 월을 초과함.");
                return false;
            }
        } else if (yearInt < currentYear) {
            // 사용자가 과거 연도를 입력한 경우 (모든 월 허용)
            if (monthInt < 1 || monthInt > 12) {
                System.out.println("월은 1~12 사이여야함.");
                return false;
            }
        }

        YearMonth yearMonth = YearMonth.of(yearInt, monthInt); // YearMonth 객체 생성
        int maxDay = yearMonth.lengthOfMonth(); // 년도와 월에 따른 유효한 최대 일수
        if( (1 > dayInt) || (dayInt > maxDay) || (dayInt > currentDay)) {
            System.out.println("년과 월에 따른 유효하지 않은 day 이거나 미래 날짜");
            return false;
        }
        return true;
    }





}
