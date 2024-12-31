package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.mapper.BoardMapper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {

    private final BoardMapper boardMapper;
    public BoardService(BoardMapper boardMapper) { this.boardMapper = boardMapper; }

    // 공지사항 등록
    public boolean noticeEnroll(Board board) {
        if(boardMapper.insertNotice(board)){
            return true;
        }
        else {
            return false;
        }
    }

    // 공지사항 삭제
    public boolean noticeDelete(int noticeIdx) {
        if(boardMapper.deleteNotice(noticeIdx) > 0){

            return true;
        }
        else {
            return false;
        }
    }

    // 공지사항 수정
    public boolean noticeUpdate(Board board) {
        if (boardMapper.updateNotice(board) > 0) {
            System.out.println("공지사항 수정 성공!");
            return true;
        } else {
            System.out.println("공지사항 수정 실패!");
            return false;

        }

    }

/*    // 공지사항 수정
    public boolean noticeUpdate(Board board) {

    }*/





    // 공지사항 총 게시글 개수 가져오기
    public int noticeAllCount(){
         int count = boardMapper.selectNoticeCount();
        return count;
    }

    // 공지사항 목록 불러오기
    public Map<String, Object> getNotices(int curpage, int size) {
        // 현재 페이지의 시작 위치 계산
        int offset = curpage * size;

        // 데이터 조회
        List<Map<String, Object>> items = boardMapper.selectNotices(size, offset); // items는 List<Map> 구조로 가정

        // 각 아이템에 닉네임 추가
        for (Map<String, Object> item : items) {
            String author = (String) item.get("author"); // boardAuthor 값을 가져옴
            System.out.println("가져온 Author: " + author);
            String nickname = getNicknameById(author); // 닉네임 가져오기 서비스 호출

            System.out.println("가져온 닉네임: " + nickname);
            item.put("nickname", nickname); // 닉네임 추가
        }

        // 전체 페이지 수 계산
        int totalNotices = boardMapper.selectNoticeCount();
        int maxpage = (int) Math.ceil((double) totalNotices / size);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);    // 닉네임이 추가된 데이터 목록
        response.put("maxpage", maxpage); // 전체 페이지 수

        System.out.println("Size: " + size + " / Offset: " + offset);
        System.out.println("Items: " + items);

        return response;
    }



    // userId로 nick_name 가져오기
    public String getNicknameById(String userId){
        return boardMapper.selectUserIdToUserNm(userId);
    }




/*    // 공지사항 목록 불러오기
    public Map<String, Object> getNotices(int curpage, int size) {
        // 현재 페이지의 시작 위치 계산
        int offset = curpage * size;

        // 데이터 조회
        List<Object> items = boardMapper.selectNotices(size, offset);

        // 전체 페이지 수 계산
        int totalNotices = boardMapper.selectNoticeCount();
        int maxpage = (int) Math.ceil((double) totalNotices / size);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);    // 현재 페이지의 데이터 목록
        response.put("maxpage", maxpage); // 전체 페이지 수

        System.out.println("Size: " + size + " / Offset: " + offset);

        return response;
    }*/




}


/*
    // 공지사항 목록 불러오기
    public List<Board> selectNoticeList() {




    }*/






