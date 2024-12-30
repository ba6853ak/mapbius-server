package com.example.mapbius_server.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {

    private int boardIdx;

    private String boardTitle;

    private String boardContent;

    private String boardAuthor;

    private String boardCreatedDate;

    private String boardUpdatedDate;
}

