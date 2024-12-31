package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.mapper.BoardMapper;
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

    // 공지사항 등록 서비스
    public boolean noticeEnroll(Board board) {
        if(boardMapper.insertNotice(board)){
            return true;
        }
        else {
            return false;
        }
    }

    // 공지사항 총 게시글 개수 가져오기
    public int noticeAllCount(){
         int count = boardMapper.selectNoticeCount();
        return count;
    }

    public Map<String, Object> getNotices(int curpage, int size) {

        int offset = curpage * size; // 현재 페이지의 시작 위치 계산

        // 데이터 조회
        List<Object> items = boardMapper.selectNotices(size, offset);

        // 전체 페이지 수 계산
        int totalNotices = boardMapper.selectNoticeCount();
        int maxpage = (int) Math.ceil((double) totalNotices / size);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("maxpage", maxpage);

        System.out.println("Size: " + size + " / Offset: " + offset);

        return response;
    }



}


/*
    // 공지사항 목록 불러오기
    public List<Board> selectNoticeList() {




    }*/






