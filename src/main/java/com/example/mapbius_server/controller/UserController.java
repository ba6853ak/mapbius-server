package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.IdRequest;
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
    ResponseData responseData;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }


    // 회원가입 매핑
    @PostMapping("/api/public/join")
    public ResponseEntity<?> joinProcess(@RequestBody User user) {
        ResponseData responseData = new ResponseData();

        if(userService.isValidId(user.getId())) {
            if (userService.insertUser(user)) {
                responseData.setCode(200);
                responseData.setMessage("회원가입 성공");
                return ResponseEntity.ok(responseData);
            } else {
                responseData.setCode(401);
                responseData.setMessage("회원가입 실패");
                return ResponseEntity.status(401).body(responseData);
            }
        } else {
            responseData.setCode(409);
            responseData.setMessage("이미 존재하거나, 사용할 수 없는 아이디입니다.");
            return ResponseEntity.status(409).body(responseData);

        }


            }





    // 회원가입 아이디 중복 검사 매핑
    @PostMapping("/api/public/join-id-check")
    public ResponseEntity<?> idCheck(@RequestBody User user) {
        ResponseData responseData = new ResponseData();
        if (userService.isIdAvailable(user.getId())) {
            responseData.setCode(200);
            responseData.setMessage("사용 가능한 ID입니다.");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode(409);
            responseData.setMessage("이미 사용중인 아이디입니다.");
            return ResponseEntity.status(409).body(responseData);
        }
    }

    // 회원가입 이메일 중복 검사 매핑
    @PostMapping("/api/public/join-email-check")
    public ResponseEntity<?> emailCheck(@RequestBody User user) {
        ResponseData responseData = new ResponseData();
        if (userService.isEmailAvailable(user.getEmail())) {
            responseData.setCode(200);
            responseData.setMessage("사용 가능한 이메일입니다.");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode(409);
            responseData.setMessage("이미 사용중인 이메일입니다.");
            return ResponseEntity.status(409).body(responseData);
        }
    }



}
