package com.example.mapbius_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rootDir = System.getProperty("user.dir");

        // 프로필 이미지 업로드 경로
        String profileUploadPath = rootDir + "/upload/profile/";
        System.out.println("Profile upload path: " + profileUploadPath);

        // 커버 이미지 업로드 경로
        String coverImageUploadPath = rootDir + "/upload/cover_image/";
        System.out.println("Cover image upload path: " + coverImageUploadPath);

        // 리소스 핸들러 등록
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:" + profileUploadPath);

        registry.addResourceHandler("/uploads/cover_images/**")
                .addResourceLocations("file:" + coverImageUploadPath);
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // React가 실행 중인 도메인
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}