package com.example.mapbius_server.controller;

import com.example.mapbius_server.service.DataService;
import com.example.mapbius_server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class DataController {

    private final DataService dataService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }


    @GetMapping(value = "/api/public/kr-data/festival-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchFestivalData(@RequestParam(required = false) String region) {
        // 요청 검증
        if (region == null || region.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "지역명을 입력해주세요."));
        }

        try {
            // 서비스 호출 (fetchFestivalData)
            ResponseEntity<Map<String, Object>> response = dataService.fetchFestivalData(region);

            // JSON 응답으로 강제 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("축제 데이터 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "내부 서버 오류", "details", e.getMessage()));
        }
    }



}
