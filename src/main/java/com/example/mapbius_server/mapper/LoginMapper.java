package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.LoginLog;
import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {

    // 사용자 로그인
    User authenticate(@Param("id") String id);

    /**
     * 로그인 후 사용자 정보 가져오기
     */
    User getUserInformation(@Param("id") String id);

    // 관리자 로그인 확인
    int selectAdminUserCheck(String id);

    int selectDeActivateCheck(@Param("id") String id);

    // 사용자 로그인 로그 기록
    int insertLoginLog(LoginLog loginLog);



}