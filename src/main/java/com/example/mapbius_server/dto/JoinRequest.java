package com.example.mapbius_server.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class JoinRequest {

    private String id; // 아이디
    private String pw; // 암호화된 비밀번호
    private String email; // 이메일
    private String nickName; // 닉네임
    private String birthDate; // 생년월일
    private String gender; // 성별 (male, female)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
