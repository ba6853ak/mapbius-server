package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;

import com.example.mapbius_server.dto.JoinRequest;
import com.example.mapbius_server.mapper.UserMapper;
import org.springframework.stereotype.Service;

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

    public boolean isJoinValid(User user) {
        if(user.getId().equals("")){
            return false;
        } else {
            return true;
        }
    }

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

    /**
     *
     * 길이 5~20, 공백 불가, 소문자, 숫자, 특수문자( -, _ )만 사용 가능
     *
     */
    public boolean isValidId(String setId){
        if (setId == null || setId.equals("")) {
            System.out.println("비어 있는 문자");
            return false;
        }
        if ((setId.length() < 5 || setId.length() > 20) ) {
            System.out.println("글자 길이");
            return false;
        }
        if (!setId.matches("^[a-z0-9_-]+$")) {
            System.out.println("문자 허용 범위");
            return false;
        }
        if (!isIdAvailable(setId)) {
            System.out.println("중복된 아이디");
            return false;
        }
        System.out.println("유효성 검사 통과 true 반환");
        return true;
    }

    public boolean isValidPw(String setPw){
        if (setPw == null || setPw.equals("")) {
            System.out.println("비어 있는 문자");
            return false;
        }
        if ((setPw.length() < 5 || setPw.length() > 20) ) {
            System.out.println("글자 길이");
            return false;
        }
        if (!setPw.matches("^[a-z0-9_-]+$")) {
            System.out.println("문자 허용 범위");
            return false;
        }
        if (!isIdAvailable(setPw)) {
            System.out.println("중복된 아이디");
            return false;
        }
        System.out.println("유효성 검사 통과 true 반환");
        return true;
    }






}
