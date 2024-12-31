package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.service.BoardService;
import com.example.mapbius_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BoardController {

    private final BoardService boardService;

    public final JwtUtil jwtUtil;

    ResponseData responseData;
    @Autowired
    public BoardController(BoardService boardService) { this.jwtUtil = new JwtUtil(); this.boardService = boardService; }


/*    @GetMapping("/api/public/notices")
    public ResponseEntity<ResponseData> getNotices() {
        responseData = new ResponseData();

    }*/


    @GetMapping("/api/public/notices")
    public Map<String, Object> getNotices(
            @RequestParam(defaultValue = "0") int curpage, // 현재 페이지
            @RequestParam(defaultValue = "5") int size    // 한 페이지에 표시할 게시글 수
    ) {
        return boardService.getNotices(curpage, size);
    }




    @PostMapping("/api/private/notice-post")
    public ResponseEntity<?> noticePost(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Board board) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String jwtLog = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        System.out.println("공지사항 동작");
        System.out.println("Extracted Username from JWT: " + jwtLog);

        ResponseData responseData = new ResponseData();
        System.out.println("Board Title: " + board.getBoardTitle());
        System.out.println("Board Content: " + board.getBoardContent());
        System.out.println("Board Author: " + board.getBoardAuthor());
        if (boardService.noticeEnroll(board)) {
            responseData.setCode(201);
            responseData.setMessage("공지사항 등록 성공");
            System.out.println("공지사항 등록 성공");
            return ResponseEntity.status(201).body(responseData);
        } else {
            System.out.println("공지사항 동작");
            System.out.println("Extracted Username from JWT: " + jwtLog);
            responseData.setCode(400);
            responseData.setMessage("공지사항 등록 실패");
            System.out.println("공지사항 등록 실패");
            return ResponseEntity.status(400).body(responseData);
        }

    }




}
