package com.fitnessmedical.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessmedical.common.AiServiceUnavailableException;
import com.fitnessmedical.config.AiProperties;
import com.fitnessmedical.dto.ai.AskRequest;
import com.fitnessmedical.dto.ai.AskResponse;

/**
 * [공부/면접] Spring → FastAPI RAG 질문 중계
 *
 * <p>JDK {@link HttpClient}로 JSON 문자열을 직접 POST한다.
 * RestTemplate/RestClient 메시지 컨버터 이슈를 피하고 body 누락을 막는다.</p>
 */
@Service
public class AiAskService {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiAskService(AiProperties aiProperties, ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public AskResponse ask(AskRequest request) {
        int nResults = request.nResults() == null ? 5 : request.nResults();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("query", request.query());
        payload.put("n_results", nResults);

        final String jsonBody;
        final String url;
        try {
            jsonBody = objectMapper.writeValueAsString(payload);
            url = UriComponentsBuilder
                    .fromUriString(aiProperties.getBaseUrl())
                    .path("/ask")
                    .toUriString();
        } catch (Exception exception) {
            throw new AiServiceUnavailableException("AI 요청을 준비하지 못했습니다.", exception);
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() >= 400) {
                throw new AiServiceUnavailableException(
                        "AI 서비스 응답에 실패했습니다. ("
                                + response.statusCode()
                                + ": "
                                + response.body()
                                + ")"
                );
            }
            AskResponse body = objectMapper.readValue(response.body(), AskResponse.class);
            if (body == null) {
                throw new AiServiceUnavailableException("AI 서비스가 빈 응답을 반환했습니다.");
            }
            return body;
        } catch (AiServiceUnavailableException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AiServiceUnavailableException(
                    "AI 서비스 호출이 중단되었습니다.",
                    exception
            );
        } catch (IOException exception) {
            throw new AiServiceUnavailableException(
                    "AI 서비스에 연결되지 않았습니다. FastAPI·Ollama 실행 여부를 확인하세요.",
                    exception
            );
        } catch (Exception exception) {
            throw new AiServiceUnavailableException(
                    "AI 서비스 응답을 해석하지 못했습니다.",
                    exception
            );
        }
    }
}
