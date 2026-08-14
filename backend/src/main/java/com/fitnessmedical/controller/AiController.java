package com.fitnessmedical.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitnessmedical.dto.ai.AskRequest;
import com.fitnessmedical.dto.ai.AskResponse;
import com.fitnessmedical.service.AiAskService;

/**
 * [공부/면접] AI 생활 안내 질문 API
 *
 * <p>의료 진단·처방을 하지 않는다. FastAPI 프롬프트 가드레일과 동일한 전제다.</p>
 *
 * <pre>
 * POST /api/ai/ask
 * → (Security: 로그인 세션 필요 — SecurityConfig에서 [직접구현])
 * → AiAskService
 * → RestClient POST {AI_BASE_URL}/ask
 * → FastAPI (Chroma 검색 + Ollama)
 * </pre>
 *
 * <p>이 Controller에 hasRole을 넣지 않는다. URL 권한은 SecurityFilterChain 책임이다.</p>
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiAskService aiAskService;

    public AiController(AiAskService aiAskService) {
        this.aiAskService = aiAskService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return aiAskService.ask(request);
    }
}
