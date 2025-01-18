package com.example.mapbius_server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class OpenAiService {

    @Value("${openai.chat.key}")
    private String apiKey;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public String getChatResponse(String userQuery) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 데이터 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant providing travel, location, and food information."),
                Map.of("role", "user", "content", userQuery)
        ));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // 요청 객체 생성
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, request, Map.class);

            // 응답에서 메시지 추출
            if (response.getBody() != null && response.getBody().get("choices") != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    logger.info("오픈 AI 메시지 반환 성공!");
                    return (String) message.get("content");
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error Response: " + e.getResponseBodyAsString());
            throw e;
        }

        return "응답을 가져오지 못했습니다. 다시 시도해주세요.";
    }
}
