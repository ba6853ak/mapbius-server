package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FindMapper {

    String selectFindEmail(String email);

    String selectUserId(String email);

}