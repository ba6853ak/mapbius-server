package com.example.mapbius_server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Service
public class NaverAPIService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Object naverSearchMethod(String query) throws URISyntaxException, UnsupportedEncodingException, JsonProcessingException {

        StringBuilder url = new StringBuilder("https://openapi.naver.com/v1/search/news.json");
        url.append("?").append("query").append("=").append(URLEncoder.encode(query, "UTF-8"));
        url.append("&").append("display").append("=").append("100");
        url.append("&").append("start").append("=").append("1");
        url.append("&").append("sort").append("=").append("date"); // 또는 sim(정확도)
        URI uri = new URI(url.toString());



        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", "IIMt13_aQoxRWtuzeWtc"); // 네이버 Client ID
        headers.set("X-Naver-Client-Secret", "Dbx_EpLClx"); // 네이버 Client Secret


        HttpEntity<String> entity = new HttpEntity<>(headers);


        ResponseEntity<String> response = restTemplate.exchange(
                uri,                 // 요청 URL
                HttpMethod.GET,      // HTTP 메서드
                entity,              // 요청 헤더와 바디
                String.class         // 응답 타입
        );


        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonResponse = (Map<String, Object>) objectMapper.readValue(response.getBody(), Object.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) jsonResponse.get("items");


        return items;
    }
}
