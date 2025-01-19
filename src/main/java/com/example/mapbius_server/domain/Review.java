package com.example.mapbius_server.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
public class Review {

    private int reviewId;
    private String userId;
    private String phoneNumber;
    private String content;
    private String reviewDate;
    private double rating;
    private String coverImage;
    private MultipartFile imageFile;

}
