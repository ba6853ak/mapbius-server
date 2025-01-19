package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.LoginLog;
import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.dto.LoginRequest;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.service.LoginService;
import com.example.mapbius_server.service.UserService;
import com.example.mapbius_server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginMapper loginMapper;



    @Autowired
    public LoginController(LoginService loginService, LoginMapper loginMapper) {
        this.loginService = loginService;
        this.jwtUtil = new JwtUtil();

        this.loginMapper = loginMapper;
    }

    public String getClientIp(HttpServletRequest request) {
        // X-Forwarded-For 헤더에서 첫 번째 IP 주소를 가져옴 (가장 원래의 클라이언트 IP)
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();  // X-Forwarded-For 헤더가 없으면 내부 IP를 사용
        } else {
            // 여러 IP가 콤마로 구분되어 있을 수 있음, 첫 번째 IP를 사용
            ipAddress = ipAddress.split(",")[0];
        }


        return ipAddress;
    }



    @PostMapping("/api/public/login")
    public ResponseEntity<?> handleLogin(@RequestBody User loginRequest, HttpServletRequest request) {

        User userData;
        ResponseData responseData = new ResponseData();

        LoginLog loginLog = new LoginLog();



        String id = loginRequest.getId();
        String pw = loginRequest.getPw();

        // 로그인 로그 기록
        loginLog.setUserId(id);

        String outIP = getClientIp(request);
        logger.info("접속을 요청한 외부 IP: " + outIP);
        loginLog.setIpAddress(outIP);
        // loginLog.setIpAddress(request.getRemoteAddr());

        boolean loginSuccess = loginService.login(id, pw);


        logger.info("login 요청한 아이피: " + request.getRemoteAddr());

        if (loginSuccess) {
            boolean isAdmin = loginService.adminCheck(id); // 관리자인가?
            String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";             // JWT 생성 (관리자라면 ROLE_ADMIN 추가)
            String userState; // 사용자 상태 관리 -> 활성화 / 비활성화

            if(loginMapper.selectDeActivateCheck(id) > 0){ // 비활성화 상태
                userState = "deactivate";
            } else { // 활성화 상태
                userState = "activate";
            }

            if(userState.equals("deactivate")){

                loginLog.setSuccess(false); // 로그인 실패
                loginService.saveLoginLog(loginLog); // 로그인 로그 기록

                logger.info("비활성화 계정의 로그인이 차단되었습니다.");
                responseData.setCode(423);
                responseData.setMessage("비활성화 계정의 로그인이 차단되었습니다.");
                responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
                return ResponseEntity.status(423).body(responseData);
            }

            String jwtToken = jwtUtil.generateJWTToken(id, role, null, userState);             // 일반 로그인이므로 카카오 로그인 시의 닉네임은 쓰지 않음.
            responseData.setCode(200);
            responseData.setMessage("로그인 성공!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            responseData.setToken(jwtToken);

            userData = loginService.getUserInfo(id);
            userData.getId();


            loginLog.setSuccess(true); // 로그인 실패
            loginService.saveLoginLog(loginLog); // 로그인 로그 기록



            responseData.setObjData(userData.getId());
            return ResponseEntity.ok(responseData);
        } else {

            loginLog.setSuccess(false); // 로그인 실패
            loginService.saveLoginLog(loginLog); // 로그인 로그 기록

            System.out.println("아이디 또는 비밀번호가 잘못 되었습니다.");
            responseData.setCode(401);
            responseData.setMessage("로그인 실패!");
            responseData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(401).body(responseData);
        }
    }




}
