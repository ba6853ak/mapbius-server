package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.LoginRequest;
import com.example.mapbius_server.service.LoginService;
import com.example.mapbius_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
public class LoginController {

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
            System.out.println("Login successful");

            // 관리자인지 확인
            boolean isAdmin = loginService.adminCheck(id);
            // JWT 생성 (관리자라면 ROLE_ADMIN 추가)
            String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";
            String jwtToken = jwtUtil.generateTokenWithRole(id, role);
            responseData.setCode(200);
            responseData.setMessage("로그인 성공");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            responseData.setToken(jwtToken);

            userData = loginService.getUserInfo(id, pw);
            System.out.println(userData);
            responseData.setObjData(userData);
            return ResponseEntity.ok(responseData);
        } else {
            System.out.println("아이디 또는 비밀번호가 잘못 되었습니다.");
            return ResponseEntity.status(401).body("");
        }
    }




}
