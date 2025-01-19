package com.example.mapbius_server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
// @JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드를 JSON 출력에서 제외
public class Review {

    private int reviewId;
    private String userId;
    private String phoneNumber;
    private String content;
    private String reviewDate;
    private double rating;
    private String coverImage;
    private MultipartFile imageFile;
    private String userNm; // 사용자 이름
}
