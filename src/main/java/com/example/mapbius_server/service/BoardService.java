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

        if ((board.getBoardTitle() == null || board.getBoardTitle().isEmpty()) ||
                (board.getBoardContent() == null || board.getBoardContent().isEmpty())) {
            return false;
        }

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
            System.out.println("공지사항 삭제 성공!");
            return true;
        }
        else {
            System.out.println("공지사항 삭제 실패!");
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

    // 공지사항 상세보기
    public Board noticeDetail(int noticeIdx) {
        Board boardResult = boardMapper.selectNoticeDetail(noticeIdx);

        if (boardResult != null) {
            String boardAuthor = boardResult.getBoardAuthor();
            if (boardAuthor != null) {
                String nickName = boardMapper.selectUserIdToUserNm(boardAuthor);
                boardResult.setNickName(nickName);
            }
            return boardResult;
        } else {
            return null;
        }
    }




    // 공지사항 목록 불러오기
    public Map<String, Object> getNotices(int curpage, int size, String keyword, String type) {
        int offset = curpage * size; // 현재 페이지의 시작 위치 계산

        // 데이터 조회
        List<Map<String, Object>> items;
        if (keyword != null && !keyword.isEmpty()) {
            // 검색 조건이 있을 때
            if ("title".equalsIgnoreCase(type)) {
                items = boardMapper.selectNoticesByTitle(keyword, size, offset);
            } else if ("content".equalsIgnoreCase(type)) {
                items = boardMapper.selectNoticesByContent(keyword, size, offset);
            } else {
                items = boardMapper.selectNoticesByTitleOrContent(keyword, size, offset);
            }
        } else {
            // 검색 조건이 없을 때 전체 공지사항 조회
            items = boardMapper.selectNotices(size, offset);
        }

        // 닉네임 추가
        for (Map<String, Object> item : items) {
            String author = (String) item.get("author");
            String nickname = boardMapper.selectUserIdToUserNm(author);
            item.put("nickname", nickname);
        }

        // 총 게시글 수 조회
        int totalNotices = (keyword != null && !keyword.isEmpty()) ?
                boardMapper.selectNoticeCountByKeyword(keyword, type) :
                boardMapper.selectNoticeCount();

        System.out.println("키워드 : " + keyword);
        System.out.println("타입 : " + type);
        System.out.println("전체 수: " + totalNotices);
        System.out.println("페이지 당 게시글 수: " + size);
        // 전체 페이지 수 계산
        int maxpage = (int) Math.ceil((double) totalNotices / size);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);    // 공지사항 데이터 목록
        response.put("maxpage", maxpage); // 전체 페이지 수

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






