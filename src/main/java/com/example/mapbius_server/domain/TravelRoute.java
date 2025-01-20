package com.example.mapbius_server.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TravelRoute {

    private Long id; // 고유 ID

    private String creatorId; // 작성자 ID (users 테이블과 연관)

    private MultipartFile imageFile;

    private String creatorNickName;

    private String coverImageName; // 커버 이미지 URL

    private String title; // 리스트 제목

    private String content; // 리스트 설명

    private String createdAt; // 생성 날짜 (기본값: 현재 시각)

    private Boolean isPrivate = false; // 프라이빗 여부 (기본값: false)

    private String locationInfo; // 위치 정보 (문자열로 이어붙임)

    private int heartCount;

    private int distances;

}
