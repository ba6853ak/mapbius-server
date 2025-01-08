package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * user 테이블의 모든 데이터를 조회합니다.
     * @return User 객체 리스트
     */
    List<User> findAll();

    void insertUser(User user);

    int selectUserId(String id);

    int selectUserEmail(String email);

    int selectUserNickName(String nm);

    int insertKakaoUser(User user);

    int selectKakaoId(String id);

    // 이메일로 비밀번호 변경
    int updatePasswordByEmail(String email, String pw);

    // 사용자 강제 삭제
    int deleteUser(@Param("id") String id);


}