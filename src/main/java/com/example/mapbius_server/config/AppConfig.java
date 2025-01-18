package com.example.mapbius_server.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 기존 컨버터 가져오기
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(restTemplate.getMessageConverters());

        // 필요한 컨버터 추가
        messageConverters.add(new FormHttpMessageConverter()); // Form 데이터 처리
        messageConverters.add(new MappingJackson2HttpMessageConverter()); // JSON 처리
        messageConverters.add(new MappingJackson2XmlHttpMessageConverter(new XmlMapper())); // XML 처리

        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

}