package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.JoinRequest;
import com.example.mapbius_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/api/public/join")
    public ResponseEntity<?> join(@RequestBody JoinRequest jq) {
         ResponseData responseData = new ResponseData();

        if (userService.insertUser(jq)) {
            responseData.setCode(200);
            responseData.setMessage("회원가입 성공");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode(401);
            responseData.setMessage("회원가입 실패");
            return ResponseEntity.ok(responseData);
        }
    }
}
