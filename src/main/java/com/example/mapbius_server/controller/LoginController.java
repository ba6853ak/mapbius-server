package com.example.mapbius_server.controller;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.LoginRequest;
import com.example.mapbius_server.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LoginController {

    public final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }



    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody LoginRequest loginRequest) {
        String id = loginRequest.getId();
        String pw = loginRequest.getPw();

        boolean loginSuccess = loginService.login(id, pw);
        if (loginSuccess) {
            System.out.println("Login successful");
            return ResponseEntity.ok().body(Map.of("message", "로그인 성공"));
        } else {
            System.out.println("Login Failed");
            return ResponseEntity.status(401).body(Map.of("message", "아이디 또는 비밀번호가 잘못 되었습니다."));
        }
    }

}
