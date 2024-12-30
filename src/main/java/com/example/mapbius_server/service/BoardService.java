package com.example.mapbius_server.service;

import com.example.mapbius_server.domain.Board;
import com.example.mapbius_server.mapper.BoardMapper;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private final BoardMapper boardMapper;
    public BoardService(BoardMapper boardMapper) { this.boardMapper = boardMapper; }

    public boolean noticeEnroll(Board board) {
        if(boardMapper.insertNotice(board)){

            return true;
        }
        else {
            return false;
        }

    }




}
