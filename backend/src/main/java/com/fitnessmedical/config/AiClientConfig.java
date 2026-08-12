package com.fitnessmedical.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * [공부/면접] AI 설정 바인딩 활성화
 *
 * <p>{@code fitness.ai.base-url} → {@link AiProperties}.
 * HTTP 호출은 JDK HttpClient({@code AiAskService})를 쓴다.</p>
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiClientConfig {
}
