package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.mapper.UserMapper;
import com.example.mapbius_server.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final LoginMapper loginMapper;
    private final PasswordUtil passwordUtil;

    public LoginService(LoginMapper loginMapper, PasswordUtil passwordUtil) {
        this.loginMapper = loginMapper;
        this.passwordUtil = passwordUtil;
    }






    public boolean login(String id, String pw) {

        User userIdAndPw = loginMapper.authenticate(id);

        if(userIdAndPw == null) {
            return false;
        }

        // String encodePw = passwordUtil.encodePassword(pw); // 평문 -> 비문 변환
        boolean loginResult = passwordUtil.checkPassword(pw, userIdAndPw.getPw()); // 평문과 암호화로 로그인 결과
        if(loginResult && userIdAndPw != null) {
            logger.info("일반 로그인 성공");
            return true;
        }
        else{
            logger.info("일반 로그인 실패");
            return false;
        }
    }

    public User getUserInfo(String id) {
        User user = loginMapper.getUserInformation(id);
        return user;
    }

    public boolean adminCheck(String id) {
        if(loginMapper.selectAdminUserCheck(id) > 0){
            return true;
        } else {
            return false;
        }
    }


}
