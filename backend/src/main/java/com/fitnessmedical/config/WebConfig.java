package com.fitnessmedical.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * [공부/면접] CORS(교차 출처 리소스 공유) 설정
 *
 * Q. CORS가 필요한 이유?
 * A. 브라우저는 보안상 "다른 출처(origin)"로의 AJAX/fetch를 기본 차단한다.
 *    React(localhost:5173)와 Spring(localhost:8080)은 포트가 달라 다른 출처이므로
 *    서버가 Access-Control-* 헤더로 허용 origin을 명시해야 한다.
 *
 * Q. allowCredentials(true)와 allowedOrigins("*")를 같이 쓰면?
 * A. 브라우저가 거부한다. 쿠키·세션을 보낼 때는 구체 origin 목록을 지정해야 한다.
 *
 * Q. SecurityConfig의 .cors()와의 관계?
 * A. WebConfig가 CorsRegistry에 정책을 등록하고, SecurityFilterChain의 cors()가
 *    preflight(OPTIONS)와 실제 요청에 그 정책을 적용한다. 둘 다 있어야 브라우저 호출이 통과한다.
 *
 * 프론트엔드 개발 서버와 백엔드의 교차 출처 요청(CORS)을 설정합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // /api/** 만 허용 — 정적 리소스·H2 콘솔 등 불필요한 경로까지 열지 않음
        registry.addMapping("/api/**")
                // localhost / 127.0.0.1 은 브라우저에서 서로 다른 origin
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:5174"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                // 세션 쿠키(JSESSIONID)를 cross-origin 요청에 포함 — 로그인 상태 유지용
                .allowCredentials(true);
    }
}
