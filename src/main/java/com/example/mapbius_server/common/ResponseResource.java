package com.example.mapbius_server.common;

import lombok.Data;
import org.springframework.core.io.Resource; // 리소스 파일 처리

import java.sql.Timestamp;

@Data
public class ResponseResource {

    private int code;
    private String message;
    private Resource resource;
    private Timestamp timestamp;
    private String token;


    public ResponseResource() {
        this.code = 200;
        this.message = "OK";
        this.resource = null;
        this.timestamp = null;
        this.token = null;
    }

}
