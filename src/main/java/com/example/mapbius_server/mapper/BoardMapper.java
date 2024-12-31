package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    boolean insertNotice(Board board);


    // 공지사항 목록 조회 (페이지네이션 포함)
    List<Object> selectNotices(@Param("size") int size, @Param("offset") int offset);

    // 공지사항 전체 개수 조회
    int selectNoticeCount();


    boolean selectNoticeList(Board board);

    boolean selectBoardDetail(Board boardRequest);

    boolean deleteBoard(Board boardRequest);

    boolean updateBoard(Board boardRequest);

    boolean selectBoardList(Board boardRequest);


}
