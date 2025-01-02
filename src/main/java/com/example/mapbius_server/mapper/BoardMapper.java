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

   /* // 공지사항 목록 조회 (페이지네이션 포함)
    List<Map<String, Object>> selectNotices(@Param("size") int size, @Param("offset") int offset);*/

    // 공지사항 전체 개수 조회
    int selectNoticeCount();

    // 사용자 아이디 -> 사용자 닉네임 전환
    String selectUserIdToUserNm(String id);

    // 공지사항 게시글 상세보기
    Board selectNoticeDetail(int id);

    // 공지사항 목록 조회 (페이징)
    List<Map<String, Object>> selectNotices(int size, int offset);

    // 제목으로 검색 (페이징)
    List<Map<String, Object>> selectNoticesByTitle(String keyword, int size, int offset);

    // 내용으로 검색 (페이징)
    List<Map<String, Object>> selectNoticesByContent(String keyword, int size, int offset);

    // 제목 또는 내용으로 검색 (페이징)
    List<Map<String, Object>> selectNoticesByTitleOrContent(String keyword, int size, int offset);


    // 검색 조건에 따른 공지사항 수 조회
    int selectNoticeCountByKeyword(String keyword, String type);

    // 공지사항 전체 검색


    // 공지사항 제목으로 검색


    // 공직사항 내용으로 검색




}
