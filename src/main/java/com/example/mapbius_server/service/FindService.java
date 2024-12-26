package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.User;
import com.example.mapbius_server.mapper.FindMapper;
import com.example.mapbius_server.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class FindService {

    private final FindMapper findMapper;

    public FindService(FindMapper findMapper) {
        this.findMapper = findMapper;
    }

    public String findEmail(String email) {
        if(findMapper.selectFindEmail(email).equals(email)) {
            return findId(email);
        }
        return "";
    }


    public String findId(String email){
        return findMapper.selectUserId(email);
    }



}
