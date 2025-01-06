package com.example.mapbius_server.controller;


import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.service.AccountService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    AccountService accountService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    // 계정 삭제
    @PostMapping("/api/private/account/delete")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String header) {
        ResponseData responseData = new ResponseData();

        if(accountService.deleteAccount(header)){
            responseData.setCode(200);
            responseData.setMessage("계정 삭제 성공!");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(404);
            responseData.setMessage("이미 삭제되었거나 정상적인 요청이 아닙니다.");
            return ResponseEntity.status(404).body(responseData);
        }

    }

}
