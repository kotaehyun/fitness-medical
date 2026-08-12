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
 * → AiAskService
 * → RestClient POST {AI_BASE_URL}/ask
 * → FastAPI (Chroma 검색 + Ollama)
 * </pre>
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
