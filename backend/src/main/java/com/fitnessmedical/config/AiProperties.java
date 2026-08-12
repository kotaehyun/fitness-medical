package com.fitnessmedical.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * [공부/면접] FastAPI AI 서비스 연결 설정
 *
 * <p>Q. application.yml의 fitness.ai와 어떻게 연결되나?<br>
 * A. {@code @ConfigurationProperties(prefix = "fitness.ai")}가
 * {@code fitness.ai.base-url}을 {@code baseUrl} 필드에 바인딩한다.</p>
 *
 * <p>Q. 왜 URL을 코드에 안 박나?<br>
 * A. 로컬·맥·배포마다 호스트가 다를 수 있다. {@code AI_BASE_URL} 환경변수로 덮어쓴다.</p>
 */
@ConfigurationProperties(prefix = "fitness.ai")
public class AiProperties {

    /**
     * FastAPI 루트 URL. 예: http://127.0.0.1:8000
     */
    private String baseUrl = "http://127.0.0.1:8000";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
