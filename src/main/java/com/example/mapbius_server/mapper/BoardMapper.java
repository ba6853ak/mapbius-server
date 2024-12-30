package com.example.mapbius_server.mapper;

import com.example.mapbius_server.domain.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    boolean insertBoard(Board boardRequest);

    boolean selectBoardDetail(Board boardRequest);

    boolean deleteBoard(Board boardRequest);

    boolean updateBoard(Board boardRequest);

    boolean selectBoardList(Board boardRequest);


}
