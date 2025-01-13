package com.example.mapbius_server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${external.api.key}")
    private String serviceKey ;

    private final String BASE_URL  = "http://apis.data.go.kr/B551011/KorService1/searchKeyword1";

    public ResponseEntity<?> fetchFestivalData(String region) {
        try {

            StringBuilder url = new StringBuilder(BASE_URL);
            url.append("?").append("numOfRows").append("=").append("5"); // 한 페이지당 데이터 개수
            url.append("&").append("MobileOS").append("=").append("ETC"); // OS 구분
            url.append("&").append("MobileApp").append("=").append("mapbius"); // 어플리케이션 이름
            url.append("&").append("keyword").append("=").append(URLEncoder.encode(region, "UTF-8")); // 검색 키워드 (인코딩 필수)
            url.append("&").append("contentTypeId").append("=").append("15"); // 관광 타입
            url.append("&").append("serviceKey").append("=").append(serviceKey); // 서비스 키 (인코딩된 값 사용)
            url.append("&").append("_type").append("=").append("json"); // 응답 형식

            URI uri = new URI(url.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type: JSON
            headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // Accept: JSON

            HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    httpRequest,
                    Map.class
            );

            // 로그 출력 (디버깅용)
            logger.info("API Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");
                if (responseBody == null || responseBody.get("body") == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "API 응답 구조가 예상과 다릅니다."));
                }

                Map<String, Object> body = (Map<String, Object>) responseBody.get("body");
                Map<String, Object> items = (Map<String, Object>) body.get("items");

                return ResponseEntity.ok(Map.of("items", items != null ? items.get("item") : List.of()));
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(Map.of("error", "API 요청 실패"));
            }

        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "URI 생성 오류", "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "알 수 없는 오류 발생", "details", e.getMessage()));
        }
    }

}
