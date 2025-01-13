package com.example.mapbius_server.controller;

import com.example.mapbius_server.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }


    @GetMapping("/kr-data/festival-info")
    public ResponseEntity<?> searchFestivalData(@RequestParam String region) {
        // 요청 검증
        if (region == null || region.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "지역명을 입력해주세요."));
        }
        return dataService.fetchFestivalData(region);
    }


}
