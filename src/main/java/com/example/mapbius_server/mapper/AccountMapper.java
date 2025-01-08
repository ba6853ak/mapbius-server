package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    int updateDeleteAccount(@Param("id") String id);

    int updateAccount(@Param("nm") String nm,
                      @Param("pw") String pw,
                      @Param("email") String email,
                      @Param("id") String id);

    String findKakaoIdByUserId(@Param("id") String userId);

    int updateNoticesIdChange(@Param("id") String id, @Param("changeId") String changeId);

}
