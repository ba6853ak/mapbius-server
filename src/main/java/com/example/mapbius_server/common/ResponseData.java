package com.example.mapbius_server.common;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ResponseData {

    private int code;
    private String message;
    private Object objData;
    private Timestamp timestamp;

    public ResponseData() {
        this.code = 200;
        this.message = "OK";
        this.objData = null;
        this.timestamp = null;
    }

}
