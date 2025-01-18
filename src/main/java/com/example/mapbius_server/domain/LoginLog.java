package com.example.mapbius_server.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoginLog {

    private Long logId; // 로그 ID (BIGINT 대응)
    private String userId; // 사용자 ID (VARCHAR(50) 대응)
    private String ipAddress; // 로그인 시 사용된 IP 주소
    private boolean success; // 로그인 성공 여부
    private LocalDateTime createdAt; // 레코드 생성 시간 (TIMESTAMP 대응)

}
