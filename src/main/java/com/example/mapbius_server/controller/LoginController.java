package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.LoginRequest;
import com.example.mapbius_server.service.LoginService;
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
    private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;

    @Autowired
    public LoginController(LoginService loginService, ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
        this.loginService = loginService;
        this.clientHttpRequestFactorySettings = clientHttpRequestFactorySettings;
    }



    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody User loginRequest) {

        User userData = new User();

        ResponseData responseData = new ResponseData();

        String id = loginRequest.getId();
        String pw = loginRequest.getPw();

        boolean loginSuccess = loginService.login(id, pw);




        if (loginSuccess) {

            System.out.println("Login successful");
            responseData.setCode(200);
            responseData.setMessage("로그인 성공");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));


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
