package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * user 테이블의 모든 데이터를 조회합니다.
     * @return User 객체 리스트
     */
    List<User> findAll();
}