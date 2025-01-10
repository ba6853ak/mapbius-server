package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.GrantRoleRequest;
import com.example.mapbius_server.dto.GrantStateRequest;
import com.example.mapbius_server.service.AdminService;
import com.example.mapbius_server.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtTokenProvider jwtTokenProvider;

    // 프로필 이미지, 닉네임. 아이디, 이메일, 생년월일, 가입날짜, 성별, 권한, 계정상태

    /**
     *
     */
/*    @PostMapping("/api/private/admin/info/user")
    public ResponseEntity<ResponseData> displayUserInfo(
            @RequestHeader("Authorization") String header,
            @RequestParam(defaultValue = "1") int curpage,  // 현재 페이지
            @RequestParam(value = "keyword", required = false) String keyword // 검색어 (사용자 아이디만 검색 가능)
            ) {

        ResponseData responseData = new ResponseData();

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String role = (String) claims.get("role"); // 토큰에서 권한 추출

        if (!role.equals("ROLE_ADMIN")) { // 만약 관리자가 아니면 권한 부여 실패
            responseData.setCode(404);
            responseData.setMessage("사용자 정보를 표시 할 권한이 없습니다.");
            responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
            return ResponseEntity.status(404).body(responseData);
        } else {


            if (adminService.grantStateToUser(gStateReq)) {
                responseData.setCode(200);
                responseData.setMessage("사용자 목록을 출력합니다.");
                responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
                return ResponseEntity.status(200).body(responseData);
            } else {
                responseData.setCode(404);
                responseData.setMessage("사용자 목록 출력에 오류가 발생했습니다.");
                responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
                return ResponseEntity.status(404).body(responseData);
            }



        }

    }*/





    /**
     * < 사용자에게 활성화(activate) / 비활성화(deactivate) 권한 부여 >
     * 사용자에게 활성화, 비활성화 상태를 부여한다.
     * 활성화는 로그인 가능, 비활성화는 로그인이 차단 된 상태를 의미한다.
     * header에 있는 토큰을 확인하고 관리자임을 판단하여 권한 부여를 진행한다.
     * 요청을 받은 객체로부터 권한을 변경할 사용자의 아이디와 계정 상태을 받는다.
     */
    @PostMapping("/api/private/admin/grant/grant-state")
    public ResponseEntity<ResponseData> stateGrant(@RequestHeader("Authorization") String header, @RequestBody GrantStateRequest gStateReq) {

        ResponseData responseData = new ResponseData();

        String token = header.substring(7).trim(); // Bearer 접두사 및 공백 제거
        Claims claims = jwtTokenProvider.validateToken(token); // 검증 및 토큰 데이터 집합 추출
        String role = (String) claims.get("role"); // 토큰에서 권한 추출

        if (!role.equals("ROLE_ADMIN")) { // 만약 관리자가 아니면 권한 부여 실패
            responseData.setCode(404);
            responseData.setMessage("수정 할 권한이 없습니다.");
            responseData.setTimestamp(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
            return ResponseEntity.status(404).body(responseData);
        } else {
            if (adminService.grantStateToUser(gStateReq)) {
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
