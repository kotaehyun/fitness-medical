package com.fitnessmedical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessmedical.common.AiServiceUnavailableException;
import com.fitnessmedical.config.AiProperties;
import com.fitnessmedical.dto.ai.AskRequest;
import com.fitnessmedical.dto.ai.AskResponse;
import com.sun.net.httpserver.HttpServer;

class AiAskServiceTest {

    private HttpServer server;
    private final AtomicReference<String> receivedBody = new AtomicReference<>();
    private AiAskService aiAskService;

    @BeforeEach
    void setUp() throws IOException {
        receivedBody.set(null);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ask", exchange -> {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            receivedBody.set(new String(bytes, StandardCharsets.UTF_8));
            byte[] response = """
                    {
                      "answer":"규칙적인 수면이 도움이 됩니다.",
                      "model":"gemma4:e2b",
                      "sources":[
                        {
                          "chunk_id":"c1",
                          "document_id":"d1",
                          "chunk_index":0,
                          "title":"수면 안내",
                          "content":"같은 시간에 잠드세요.",
                          "distance":0.12
                        }
                      ]
                    }
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();

        AiProperties properties = new AiProperties();
        properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
        aiAskService = new AiAskService(properties, new ObjectMapper());
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void ask_성공하면_답변과_출처를_반환한다() {
        AskResponse response = aiAskService.ask(new AskRequest("수면 습관", 3));

        assertThat(receivedBody.get()).contains("\"query\":\"수면 습관\"");
        assertThat(receivedBody.get()).contains("\"n_results\":3");
        assertThat(response.answer()).contains("규칙적인 수면");
        assertThat(response.model()).isEqualTo("gemma4:e2b");
        assertThat(response.sources()).hasSize(1);
        assertThat(response.sources().getFirst().chunkId()).isEqualTo("c1");
    }

    @Test
    void ask_nResults가_없으면_기본값_5를_보낸다() {
        aiAskService.ask(new AskRequest("잠", null));
        assertThat(receivedBody.get()).contains("\"n_results\":5");
    }

    @Test
    void ask_AI가_503이면_AiServiceUnavailableException() throws IOException {
        server.stop(0);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ask", exchange -> {
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        server.start();

        AiProperties properties = new AiProperties();
        properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
        AiAskService service = new AiAskService(properties, new ObjectMapper());

        assertThatThrownBy(() -> service.ask(new AskRequest("질문", 5)))
                .isInstanceOf(AiServiceUnavailableException.class);
    }
}
