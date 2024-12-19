package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.LoginMapper;
import com.example.mapbius_server.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final LoginMapper loginMapper;

    public LoginService(LoginMapper loginMapper) {
        this.loginMapper = loginMapper;
    }

    public boolean login(String id, String pw) {

        User user = loginMapper.authenticate(id, pw);
        if(user!=null && user.getId().equals(id) && user.getPw().equals(pw)){
            System.out.println("true");
            return true;
        }
        else if(user==null) {
            System.out.println("false");
            return false;
        }
        else{
            return false;
        }
    }


}
