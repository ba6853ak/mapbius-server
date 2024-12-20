package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;

import com.example.mapbius_server.dto.JoinRequest;
import com.example.mapbius_server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public boolean insertUser(JoinRequest jq) {

        User user = new User();
        user.setId(jq.getId());
        user.setPw(jq.getPw());
        user.setNickName(jq.getNickName());
        user.setEmail(jq.getEmail());
        user.setBirthDate(jq.getBirthDate());
        user.setGender(jq.getGender());
        System.out.println(user);

        try{
            userMapper.insertUser(user);
            System.out.println(user);
            return true;
        } catch (NullPointerException e) {
            return false;
        }

    }
}
