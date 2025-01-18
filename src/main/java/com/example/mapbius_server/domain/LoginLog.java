package com.example.mapbius_server.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginLog {

    private String userId; // 사용자 ID (VARCHAR(50) 대응)
    private String loginTimestamp; // 로그인 시각 (String 타입으로 처리, 혹은 LocalDateTime을 사용할 수도 있음)
    private String ipAddress; // 로그인 시 사용된 IP 주소
    private boolean success; // 로그인 성공 여부
    private String createdAt; // 레코드 생성 시간 (기본적으로 현재 시간으로 자동 설정)
}