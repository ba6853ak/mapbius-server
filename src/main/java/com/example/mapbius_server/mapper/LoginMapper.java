package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {

/*    *//**
     * 아이디로 사용자 조회
     * @param id 사용자 아이디
     * @return User 객체
     *//*
    User findUserById(@Param("id") String id);

    *//**
     * 이메일로 사용자 조회
     * @param email 사용자 이메일
     * @return User 객체
     *//*
    User findUserByEmail(@Param("email") String email);*/

    /**
     * 로그인 인증
     * @param id 사용자 아이디
     * @param pw 사용자 비밀번호
     * @return User 객체 (인증 성공 시)
     */
    User authenticate(@Param("id") String id, @Param("pw") String pw);
}