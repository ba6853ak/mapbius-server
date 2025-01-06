package com.example.mapbius_server.service;

import com.example.mapbius_server.mapper.AccountMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.util.JwtTokenProvider;
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


    public boolean deleteAccount(@RequestHeader("Authorization") String header) {

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String userId = (String) claims.get("sub"); // 토큰에서 아이디 추출

        if(accountMapper.updateDeleteAccount(userId) > 0){
            return true;
        } else {
            return false;
        }




    }


}
