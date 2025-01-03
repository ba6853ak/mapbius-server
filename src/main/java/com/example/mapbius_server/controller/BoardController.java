package com.example.mapbius_server.controller;

import com.example.mapbius_server.common.ResponseData;
import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.mapper.BoardMapper;
import com.example.mapbius_server.service.BoardService;
import com.example.mapbius_server.util.JwtUtil;
import com.mysql.cj.protocol.x.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BoardController {

    private final BoardService boardService;

    public final JwtUtil jwtUtil;
    private final BoardMapper boardMapper;

    ResponseData responseData;
    @Autowired
    public BoardController(BoardService boardService, BoardMapper boardMapper) { this.jwtUtil = new JwtUtil(); this.boardService = boardService;
        this.boardMapper = boardMapper;
    }


    // 공지사항 목록 반환
    @GetMapping("/api/public/notices/")
    public ResponseEntity<ResponseData> getNotices(
            @RequestParam(defaultValue = "1") int curpage,  // 현재 페이지
            @RequestParam(value = "keyword", required = false) String keyword, // 검색어 (옵션)
            @RequestParam(value = "type", required = false) String type        // 검색 타입 (옵션: title, content)
    ) {
        final int PAGEPERARTICLESIZE = 5;
        ResponseData responseData = new ResponseData(); // ResponseData 초기화
        System.out.println("오류 발생");
        responseData.setMessage("공지사항 데이터 반환 성공!");
        responseData.setCode(200);
        responseData.setObjData(boardService.getNotices(curpage - 1, PAGEPERARTICLESIZE, keyword, type)); // 1부터 시작 변환
        return ResponseEntity.status(200).body(responseData);
    }



    // 공지사항 게시글 상세보기

    @GetMapping("/api/public/notices/{id}")
    public Object getNoticeById(@PathVariable("id") int id) {
        System.out.println("요청된 id: " + id);
        Board result = boardService.noticeDetail(id);
        ResponseData responseData = new ResponseData(); // ResponseData 초기화
        if(result != null){
            responseData.setCode(200);
            responseData.setObjData(result);
            responseData.setMessage("공지사항 상세보기 반환");
            System.out.println("공지사항 상세보기 반환");
            return ResponseEntity.status(200).body(responseData);
        } else {
            responseData.setCode(400);
            System.out.println(result);
            responseData.setObjData(result);
            responseData.setMessage("공지사항 상세보기 반환 실패");
            System.out.println("공지사항 상세보기 반환 실패");
            return ResponseEntity.status(400).body(responseData);
        }
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
