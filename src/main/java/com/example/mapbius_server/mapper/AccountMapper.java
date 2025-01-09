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


    // 사용자 조회
    User findByUserId(@Param("id") String id);

    // 프로필 이미지 업데이트
    void updateProfileImage(@Param("id") String id,
                            @Param("profileImage") String profileImage);

    // 프로필 이미지 표시
    String findProfileImageByUserId(@Param("id") String id);

}
