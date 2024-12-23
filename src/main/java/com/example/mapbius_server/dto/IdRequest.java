package com.example.mapbius_server.dto;

public class IdRequest {

    private String id;

    // 생성자, getter, setter 추가
    public IdRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
