package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.GrantRoleRequest;
import com.example.mapbius_server.service.AdminService;
import com.example.mapbius_server.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminController {



    private final AdminService adminService;
    private final JwtTokenProvider jwtTokenProvider;

    // 프로필 이미지, 닉네임. 아이디, 이메일, 생년월일, 가입날짜, 성별, 권한, 계정상태

    // 계정 상태 활성화 부여


    // 계정 상태 비활성화 부여


    // 관리자 권한 부여


    // 일반 사용자 권한 부여


    /**
     * < 사용자에게 일반(normal) / 관리자(admin) 권한 부여 >
     * 사용자에게 일반 혹은 관리자 권한을 부여한다.
     * header에 있는 토큰을 확인하고 관리자임을 판단하여 권한 부여를 진행한다.
     * 요청을 받은 객체로부터 권한을 변경할 사용자의 아이디와 역할을 받는다.
     */
    @PostMapping("/api/private/admin/grant/grant-role")
    public ResponseEntity<ResponseData> roleGrant(@RequestHeader("Authorization") String header, @RequestBody GrantRoleRequest gRoleReq) {

        ResponseData responseData = new ResponseData();

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String role = (String) claims.get("role"); // 토큰에서 권한 추출

        if(!role.equals("ROLE_ADMIN")) { // 만약 관리자가 아니면 권한 부여 실패
            responseData.setCode(404);
            responseData.setMessage("수정 할 권한이 없습니다.");
            responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
            return ResponseEntity.status(404).body(responseData);
        } else {
            if(adminService.grantRoleToUser(gRoleReq)){
                responseData.setCode(200);
                responseData.setMessage("해당 유저에게 요청하신 권한으로 수정되었습니다.");
                responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
                return ResponseEntity.status(200).body(responseData);
            } else {
                responseData.setCode(404);
                responseData.setMessage("해당 유저에게 요청하신 권한 부여에 실패 했습니다.");
                responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
                return ResponseEntity.status(404).body(responseData);
            }
        }
    }

}
