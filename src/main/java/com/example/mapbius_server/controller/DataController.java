package com.example.mapbius_server.controller;

import com.example.mapbius_server.service.DataService;
import com.example.mapbius_server.service.NaverAPIService;
import com.example.mapbius_server.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
public class DataController {

    private final DataService dataService;
    private final NaverAPIService naverAPIService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public DataController(DataService dataService, NaverAPIService naverAPIService) {
        this.dataService = dataService;
        this.naverAPIService = naverAPIService;
    }


    // 네이버 이미지 검색 서비스
    @GetMapping("/api/public/naver/image-search")
    public Object searchImage(@RequestParam String query) throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException {

        // JSON 응답으로 강제 반환
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(naverAPIService.naverImageMethod(query));

    }


    // 네이버 검색 서비스
    @GetMapping("/api/public/naver/search")
    public Object searchLocal(@RequestParam String query) throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException {

        // JSON 응답으로 강제 반환
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(naverAPIService.naverSearchMethod(query));

    }


    // 시도 별 시군구 표시하기 (Nongsaro)
    @GetMapping(value = "/api/public/kr-data/entire-sigungo-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchSidoPerSigunguDisplay(@RequestParam(required = false) String region) {
        // 요청 검증
        if (region == null || region.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "지역명을 입력해주세요."));
        }

        try {
            // 서비스 호출 (fetchFestivalData)
            ResponseEntity<Map<String, Object>> response = dataService.searchSidoPerSigunguDisplay(region);

            System.out.println(response.getBody());


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



    // 선택 지역 특산물 표시하기 (Nongsaro)
    @GetMapping(value = "/api/public/kr-data/entire-specialty-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchRegionSpecialty(@RequestParam(required = false) String region) {
        // 요청 검증
        if (region == null || region.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "지역명을 입력해주세요."));
        }

        try {
            // 서비스 호출 (fetchFestivalData)
            ResponseEntity<Map<String, Object>> response = dataService.fetchNongsaroData(region);


            System.out.println(response.getBody());


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


    // 시군구 입력 후 법정동 코드로 변환하여 인구 정보 검색.
    @GetMapping(value = "/api/public/kr-data/population-search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchRegionCodeData(@RequestParam(required = false) String region) {
        // 요청 검증
        if (region == null || region.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "지역명을 입력해주세요."));
        }

        try {
            // 서비스 호출 (fetchFestivalData)
            ResponseEntity<Map<String, Object>> response = dataService.fetchStanReginCd(region);
            String rcd = response.getBody().get("region_cd").toString();

            ResponseEntity<Map<String, Object>> finalObj = dataService.fetchPopulationData(rcd);


            System.out.println(finalObj.getBody());


            // JSON 응답으로 강제 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(finalObj.getBody());

        } catch (Exception e) {
            logger.error("축제 데이터 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "내부 서버 오류", "details", e.getMessage()));
        }
    }


    // 축제 정보
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





    @GetMapping(value = "/api/public/kr-data/population-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchPopulationData(@RequestParam(required = false) String region) {

        if (region == null || region.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "지역명을 입력해주세요."));
        }

        try {
            ResponseEntity<Map<String, Object>> response = dataService.fetchPopulationData(region);

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
