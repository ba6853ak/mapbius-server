package com.example.mapbius_server.aop;

import com.example.mapbius_server.common.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData> exception(Exception e) {
        ResponseData responseData = new ResponseData();
        responseData.setCode(500);
        responseData.setMessage("error");
        e.printStackTrace();
        return ResponseEntity.status(500).body(responseData);
    }
}
