package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.Favorite;
import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {


    // 본인 아이디로 등록한 즐겨찾기가 있는지 확인
    int checkIfFavoriteExists(Favorite favorite);


    // 즐겨찾기 등록
    int insertFavorite(Favorite favorite);

    // 즐겨찾기 삭제
    int deleteFavorite(int favIndex);

    // 본인만 보여지는 즐겨찾기 리스트
    List<Favorite> getFavoritesByUserId(String userId);


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
