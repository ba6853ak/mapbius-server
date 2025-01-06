package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.Email;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.service.EmailService;
import com.example.mapbius_server.service.FindService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtUtil;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserService userService;

    private final FindService findService;

    private final EmailService emailService;

    ResponseData responseData;

    @Autowired
    public UserController(UserService userService, FindService findService, EmailService emailService) {
        this.userService = userService;
        this.findService = findService;
        this.emailService = emailService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }


    // 회원가입 매핑
    @PostMapping("/api/public/join")
    public ResponseEntity<?> joinProcess(@RequestBody User user) {
        ResponseData responseData = new ResponseData();

        if (userService.isJoinValid(user)) {
            userService.insertUser(user);
            responseData.setCode(200);
            responseData.setMessage("회원가입 성공");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode(401);
            responseData.setMessage("회원가입 실패");
            return ResponseEntity.status(401).body(responseData);
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

    // 아이디 찾기
    @PostMapping("/api/public/forget-id")
    public ResponseEntity<?> ForgetId(@RequestBody User user) {
        ResponseData responseData = new ResponseData();
        if (!findService.findEmail(user.getEmail()).equals("")) {
            User userData = new User();
            userData.setId(findService.findEmail(user.getEmail()));
            responseData.setObjData(userData);
            responseData.setCode(200);
            responseData.setMessage("입력한 이메일로 아이디 검색됨.");
            return ResponseEntity.ok(userData);
        } else {
            responseData.setCode(409);
            responseData.setMessage("입력한 이메일로 아이디 찾을 수 없음.");
            return ResponseEntity.status(409).body(responseData);
        }
    }

    // 비밀번호 찾기 시 계정 존재 여부
    @GetMapping("/api/public/account-exist")
    public ResponseEntity<?> ForgetPwd(@RequestBody User user) throws MessagingException {
        ResponseData responseData = new ResponseData();
        if(findService.existEmail(user)) {
            responseData.setCode(200);
            responseData.setMessage("해당 정보로 가입한 계정이 존재합니다.");
            logger.info("[비밀번호 찾기] " + user.getEmail() + " 로 가입한 정보가 존재함.");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("해당 정보로 가입한 계정이 없습니다.");
            logger.info("[비밀번호 찾기] " + user.getEmail() + " 로 가입한 정보가 존재하지 않음.");
            return ResponseEntity.status(404).body(responseData);
        }
    }






























    // 비밀번호 찾기
/*    @PostMapping("/api/public/forget-pw")
    public ResponseEntity<?> ForgetPw(@RequestBody User user) {
        ResponseData responseData = new ResponseData();
        if (userService.findPw(user)) {
            responseData.setCode(200);
            responseData.setMessage("사용 가능한 이메일입니다.");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode(409);
            responseData.setMessage("이미 사용중인 이메일입니다.");
            return ResponseEntity.status(409).body(responseData);
        }
    }*/






}
