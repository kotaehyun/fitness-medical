package com.fitnessmedical.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fitnessmedical.controller.AiController;
import com.fitnessmedical.service.AiAskService;

/**
 * [직접구현] /api/ai/ask 로그인 필수 테스트
 *
 * Q. 왜 addFilters = false 를 안 쓰나?
 * A. 이 테스트 목적은 Security 필터다. 끄면 401을 검증할 수 없다.
 *
 * Q. 왜 @Import(SecurityConfig.class) 인가?
 * A. @WebMvcTest는 Controller만 띄운다. 우리 FilterChain(csrf.disable, 401 엔트리포인트,
 *    /api/ai/** authenticated)을 쓰려면 SecurityConfig를 가져와야 한다.
 *
 * Q. 로그인 없이 401인 이유는?
 * A. SecurityConfig에서 /api/ai/** 를 authenticated() 하고,
 *    미인증은 authenticationEntryPoint가 401 JSON을 준다.
 */
@WebMvcTest(AiController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class SecurityAiAuthorizationTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AiAskService aiAskService;

    @Test
    @DisplayName("로그인 없이 POST /api/ai/ask 는 401이다.")
    void ask_withoutLogin_returns401() throws Exception {
        mockMvc.perform(post("/api/ai/ask")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"잠은 어떻게 자면 좋나요?\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("로그인한 MEMBER는 POST /api/ai/ask 가 401이 아니다.")
    void ask_withMember_isNotUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ai/ask")
                        .contentType(APPLICATION_JSON)
                        .content("{\"query\":\"잠은 어떻게 자면 좋나요?\"}"))
                .andExpect(status().isOk());
    }
}
