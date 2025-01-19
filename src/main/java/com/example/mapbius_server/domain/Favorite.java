package com.example.mapbius_server.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Favorite {

    private int favIndex;

    private String userId;

    private String type;

    private String locationCode;

    private String locationName;

    private String locationAddress;

}

