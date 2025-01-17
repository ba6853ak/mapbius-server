package com.example.mapbius_server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

// 농사로
// https://www.nongsaro.go.kr/portal/portalMain.ps?menuId=PS00001

@Service
public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${external.api.key}")
    private String serviceKey;




    public ResponseEntity<Map<String, Object>> searchSidoPerSigunguDisplay(String region) {
        try {
            StringBuilder url = new StringBuilder("http://api.nongsaro.go.kr/service/localSpcprd/selectAreaSigunguLst");
            url.append("?").append("apiKey").append("=").append("20250116EOYJGPMATSLA2IMIWLKG"); // api 키
            url.append("&").append("sDoNm").append("=").append(URLEncoder.encode(region, "UTF-8")); // 지역 코드


            System.out.println("API 요청 URL: " + url.toString());

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

                Map<String, Object> bodyMap = (Map<String, Object>) topLevel.get("body");

                Map<String, Object> ItemMap = (Map<String, Object>) bodyMap.get("items");


                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ItemMap);

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





    public ResponseEntity<Map<String, Object>> fetchNongsaroData(String region) {

        try {

            StringBuilder url = new StringBuilder("http://api.nongsaro.go.kr/service/localSpcprd/localSpcprdLst");
            url.append("?").append("apiKey").append("=").append("20250116EOYJGPMATSLA2IMIWLKG"); // api 키
            url.append("&").append("sAreaCode").append("=").append(URLEncoder.encode(region, "UTF-8")); // 지역 코드


            System.out.println("API 요청 URL: " + url.toString());

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

                Map<String, Object> responseMap = (Map<String, Object>) topLevel.get("body");

                Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("items");

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



/*    public Map<String, Object> getSigunguList(String sDoNm) throws Exception {
        // 요청 URL 생성
        String encodedSDoNm = URLEncoder.encode(sDoNm, StandardCharsets.UTF_8);
        String url = "http://api.nongsaro.go.kr/service/localSpcprd/selectAreaSigunguLst" + "?apiKey=" + API_KEY + "&sDoNm=" + encodedSDoNm;

        // RestTemplate을 사용하여 API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(new URI(url), Map.class);

        // 응답 반환
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
        }
    }*/


    public ResponseEntity<Map<String, Object>> fetchStanReginCd(String region) {
        try {
            StringBuilder url = new StringBuilder("http://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList");
            url.append("?").append("serviceKey").append("=").append("sutkI4wgJIhJNsRDZsKbXxuBTvV9BR8UNt3YQrRtiItxJxD8Y9Fwq32kob4k2dL3qyLpiu%2FzOmJ0G7QtB5QKZA%3D%3D");
            url.append("&").append("pageNo").append("=").append("1");
            url.append("&").append("numOfRows").append("3");
            url.append("&").append("type").append("xml");
            url.append("&").append("locatadd_nm").append("=").append(URLEncoder.encode(region, "UTF-8"));

            URI uri = new URI(url.toString());
            System.out.println("Generated URI: " + uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    httpRequest,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                System.out.println("Raw XML Response: " + responseBody);

                // Parse XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream inputStream = new ByteArrayInputStream(responseBody.getBytes());
                Document document = builder.parse(inputStream);

                NodeList regionCdNodes = document.getElementsByTagName("region_cd");

                // Extracting the first `region_cd` value
                if (regionCdNodes.getLength() > 0) {
                    String firstRegionCd = regionCdNodes.item(0).getTextContent();
                    System.out.println("First region_cd: " + firstRegionCd);

                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("region_cd", firstRegionCd));
                } else {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("error", "region_cd 값이 없습니다."));
                }
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




    public ResponseEntity<Map<String, Object>> fetchPopulationData(String region) {

        try {

            StringBuilder url = new StringBuilder("http://apis.data.go.kr/1741000/stdgPpltnHhStus/selectStdgPpltnHhStus");
            url.append("?").append("serviceKey").append("=").append("tcugr%2BBAM6xytHFMqRIsdGCP3Tl8re%2B3sfcfxFJz7BFSI8HYjDsk62a8FXl5LGJD1rEA99GHB11OUdLItPrbVA%3D%3D");
            url.append("&").append("stdgCd").append("=").append(URLEncoder.encode(region, "UTF-8"));
            url.append("&").append("srchFrYm").append("=").append("202408"); // 조회 시작 년월
            url.append("&").append("srchToYm").append("=").append("202408"); // 조회 종료 년월
            url.append("&").append("lv").append("=").append("3"); // 시군구 단위
            url.append("&").append("regSeCd").append("=").append("1"); // 전체
            url.append("&").append("type").append("=").append("JSON"); // JSON 형식
            url.append("&").append("numOfRows").append("=").append("1000"); // 페이지당 100개
            url.append("&").append("pageNo").append("=").append("1"); // 페이지 번호

            System.out.println("API 요청 URL: " + url.toString());

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

                Map<String, Object> responseMap = (Map<String, Object>) topLevel.get("Response");

                Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("items");

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