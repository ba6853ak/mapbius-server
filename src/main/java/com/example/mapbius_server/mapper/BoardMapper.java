package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    // 공지사랑 등록
    boolean insertNotice(Board board);

    // 공지사항 삭제
    int deleteNotice(int id);

    // 공지사항 수정
    int updateNotice(Board board);

    // 공지사항 목록 조회 (페이지네이션 포함)
    List<Map<String, Object>> selectNotices(@Param("size") int size, @Param("offset") int offset);

    // 공지사항 전체 개수 조회
    int selectNoticeCount();

    // 사용자 아이디 -> 사용자 닉네임 전환
    String selectUserIdToUserNm(String id);

    // 공지사항 게시글 상세보기
    Board selectNoticeDetail(int id);

}
