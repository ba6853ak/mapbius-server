package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    int updateDeleteAccount(@Param("id") String id);


}
