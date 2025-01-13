package com.example.mapbius_server.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
    private String serviceKey;

    private final String BASE_URL = "http://apis.data.go.kr/B551011/KorService1/searchKeyword1";


    public ResponseEntity<Map<String, Object>> fetchFestivalData(String region) {
        try {

            StringBuilder url = new StringBuilder(BASE_URL);
            url.append("?").append("numOfRows").append("=").append("100");
            url.append("&").append("MobileOS").append("=").append("ETC");
            url.append("&").append("MobileApp").append("=").append("mapbius");
            url.append("&").append("keyword").append("=").append(URLEncoder.encode(region, "UTF-8"));
            url.append("&").append("contentTypeId").append("=").append("15");
            url.append("&").append("serviceKey").append("=").append(serviceKey);
            url.append("&").append("_type").append("=").append("json"); // JSON 요청

            URI uri = new URI(url.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    httpRequest,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                Map<String, Object> topLevel = response.getBody();

                Map<String, Object> responseMap = (Map<String, Object>) topLevel.get("response");

                Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyMap);

            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "error", "API 요청 실패",
                                "status", response.getStatusCode(),
                                "details", response.getBody()
                        ));
            }

        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "error", "URI 생성 오류",
                            "details", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "error", "알 수 없는 오류 발생",
                            "details", e.getMessage()
                    ));
        }
    }


}