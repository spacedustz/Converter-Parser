package com.rtsp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${hls.file.path}")
    private String hlsFilePath;

    // "file:/home/user/videos/" 디렉토리의 리소스를 "/videos/**" 경로로 서비스
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:"+hlsFilePath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해
                .allowedOrigins("*")  // 허용할 원본
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메서드
                .allowedHeaders("Header1", "Header2", "Header3")
                .exposedHeaders("Header1", "Header2")
                //.allowCredentials(true)
                .maxAge(3600);  // 1시간 동안 pre-flight 응답을 캐시
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/rtsp/*")
//                .allowedOrigins("http://localhost:3000", "http://localhost:5002") // 허용할 origin
//                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
//                .allowedHeaders("*") // 허용할 헤더
//                .allowCredentials(true); // Credentials (쿠키 등) 허용 여부
//    }
}