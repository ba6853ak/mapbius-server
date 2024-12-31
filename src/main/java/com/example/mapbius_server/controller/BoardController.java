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

    // 공지사항 목록 반환
    @GetMapping("/api/public/notices")
    public Map<String, Object> getNotices(
            @RequestParam(defaultValue = "1") int curpage, // 현재 페이지 (1부터 시작)
            @RequestParam(defaultValue = "5") int size    // 한 페이지에 표시할 게시글 수
    ) {
        // curpage를 0부터 시작하도록 변환
        int zeroBasedPage = curpage - 1;
        return boardService.getNotices(zeroBasedPage, size);
    }


    // 공지사항 게시글 등록
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


    // 공지사항 게시글 수정
    @PostMapping("/api/private/notice-update")
    public ResponseEntity<?> noticeUpdate(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Board board) {

        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        String tokenOwner = jwtUtil.validateToken(token).getSubject(); // 토큰 주인이 누구인가?
        System.out.println("공지사항 수정 동작");
        System.out.println("Extracted userId from JWT: " + tokenOwner);

        responseData = new ResponseData();

        if (boardService.noticeUpdate(board)) {
            responseData.setCode(200);
            responseData.setMessage("공지사항 수정 성공!");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(400);
            responseData.setMessage("공지사항 수정 실패!");
            return ResponseEntity.status(400).body(responseData);
        }


    }


    // 공지사항 게시글 삭제
    @PostMapping("/api/private/notice-delete")
    public ResponseEntity<?> noticeDelete(@RequestHeader("Authorization") String authorizationHeader, @RequestBody int noticeIdx) {
        String token = authorizationHeader.replace("Bearer ", "");
        String jwtLog = jwtUtil.validateToken(token).getSubject(); // 토큰 검증
        System.out.println("공지사항 삭제 동작");
        System.out.println("Extracted userId from JWT: " + jwtLog);
        responseData = new ResponseData();

        if(boardService.noticeDelete(noticeIdx)) {
            responseData.setCode(200);
            responseData.setMessage("공지사항 삭제 성공!");
            System.out.println("공지사항 삭제 성공");
            return ResponseEntity.status(200).body(responseData);
        }
        else {
            responseData.setCode(400);
            responseData.setMessage("공지사항 삭제 실패!");
            System.out.println("공지사항 삭제 실패");
            return ResponseEntity.status(400).body(responseData);
        }

    }





}
