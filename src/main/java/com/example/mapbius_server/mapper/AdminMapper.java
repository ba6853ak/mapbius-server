package com.example.mapbius_server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {

    int insertRoleAdmin(@Param("id") String id);

    int deleteRoleAdmin(@Param("id") String id);

    int updateActivateAccount(@Param("id") String id);

    int updateDeActivateAccount(@Param("id") String id);

}
