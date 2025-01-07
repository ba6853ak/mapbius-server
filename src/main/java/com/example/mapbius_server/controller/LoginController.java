package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.LoginRequest;
import com.example.mapbius_server.service.LoginService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public final LoginService loginService;

    public final JwtUtil jwtUtil;


    // private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;




    @Autowired
    // public LoginController(LoginService loginService, ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
        this.jwtUtil = new JwtUtil();
        // this.clientHttpRequestFactorySettings = clientHttpRequestFactorySettings;
    }

    @PostMapping("/api/public/login")
    public ResponseEntity<?> handleLogin(@RequestBody User loginRequest) {

        User userData;
        ResponseData responseData = new ResponseData();

        String id = loginRequest.getId();
        String pw = loginRequest.getPw();

        boolean loginSuccess = loginService.login(id, pw);

        if (loginSuccess) {
            logger.info("로그인 성공!");

            boolean isAdmin = loginService.adminCheck(id); // 관리자인가?

            String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";             // JWT 생성 (관리자라면 ROLE_ADMIN 추가)
            String jwtToken = jwtUtil.generateJWTToken(id, role, null, "activate");             // 일반 로그인이므로 카카오 로그인 시의 닉네임은 쓰지 않음.
            responseData.setCode(200);
            responseData.setMessage("로그인 성공!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            responseData.setToken(jwtToken);

            userData = loginService.getUserInfo(id);
            System.out.println(userData);
            responseData.setObjData(userData.getId());
            return ResponseEntity.ok(responseData);
        } else {
            System.out.println("아이디 또는 비밀번호가 잘못 되었습니다.");
            responseData.setCode(401);
            responseData.setMessage("로그인 실패!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(401).body(responseData);
        }
    }




}
