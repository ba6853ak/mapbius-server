package com.example.mapbius_server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    int insertOrDeleteRoleAdmin(@Param("id") String id);

    int deleteRoleAdmin(@Param("id") String id);

    int updateActivateAccount(@Param("id") String id);

    int updateDeActivateAccount(@Param("id") String id);

    /**
     * 특정 user_id가 admin_users 테이블에 존재하는지 확인합니다.
     *
     * @param id 사용자 ID
     * @return 존재하면 true, 존재하지 않으면 false
     */
    boolean existsAdminUser(@Param("id") String id);

    /**
     * admin_users 테이블에서 특정 user_id를 삭제합니다.
     *
     * @param id 사용자 ID
     */
    void deleteAdminUser(@Param("id") String id);

    /**
     * admin_users 테이블에 특정 user_id를 삽입합니다.
     *
     * @param id 사용자 ID
     */
    void insertAdminUser(@Param("id") String id);


    List<Map<String, Object>> getAllUsers();



}
